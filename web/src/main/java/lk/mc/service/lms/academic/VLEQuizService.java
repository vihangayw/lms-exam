package lk.mc.service.lms.academic;

import lk.mc.config.JobRunnerScheduleStarter;
import lk.mc.core.api.response.ResponseWrapper;
import lk.mc.core.exceptions.AuthorizationException;
import lk.mc.internationalization.service.LocaleService;
import lk.mc.model.AuditLogRequest;
import lk.mc.service.JwtUserDetailsService;
import lk.mc.std.bean.ExamPic;
import lk.mc.std.bean.ExamPreflightAudit;
import lk.mc.std.job.NotificationJobService;
import lk.mc.std.repository.ExamPreflightAuditRepository;
import lk.mc.std.repository.StudentQuizRepository;
import lk.mc.std.util.Constants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PreDestroy;

import static lk.mc.core.enums.JwtTypes.*;

/**
 * REST Controller for user requests
 *
 * @author vihangawicks
 * @since 12/14/21
 * MC-lms
 */

@SuppressWarnings("Duplicates")
@Component
public class VLEQuizService {
    private static final Logger logger = LogManager.getLogger(VLEQuizService.class);
    private final ConcurrentHashMap<Integer, ReentrantLock> studentLocks = new ConcurrentHashMap<>();

    private final LocaleService localeService;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final StudentQuizRepository studentQuizRepository;
    private final ExamPreflightAuditRepository examPreflightAuditRepository;
    private final NotificationJobService notificationJobService;
    
    // Separate thread pools for cam and screen image notifications
    private final ExecutorService camImageExecutor;
    private final ExecutorService screenImageExecutor;

    @Autowired
    public VLEQuizService(LocaleService localeService, JwtUserDetailsService jwtUserDetailsService, StudentQuizRepository studentQuizRepository, ExamPreflightAuditRepository examPreflightAuditRepository, NotificationJobService notificationJobService) {
        this.localeService = localeService;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.studentQuizRepository = studentQuizRepository;
        this.examPreflightAuditRepository = examPreflightAuditRepository;
        this.notificationJobService = notificationJobService;
        
        // Initialize thread pools for async notification processing
        // Configured for 400+ concurrent users
        // Cam image thread pool: core=50, max=100, queue=1000
        this.camImageExecutor = createThreadPool(51, 101, 1001, "cam-image-notification");
        
        // Screen image thread pool: core=50, max=100, queue=1000
        this.screenImageExecutor = createThreadPool(50, 100, 1000, "screen-image-notification");
        
        logger.info("Initialized thread pools for image notifications: cam={}, screen={}", 
                ((ThreadPoolExecutor) camImageExecutor).getCorePoolSize(),
                ((ThreadPoolExecutor) screenImageExecutor).getCorePoolSize());
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("Shutting down image notification thread pools...");
        shutdownExecutor(camImageExecutor, "cam-image");
        shutdownExecutor(screenImageExecutor, "screen-image");
    }
    
    private ThreadPoolExecutor createThreadPool(int corePoolSize, int maxPoolSize, int queueCapacity, String threadNamePrefix) {
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r, threadNamePrefix);
            t.setDaemon(true);
            return t;
        };
        
        RejectedExecutionHandler rejectionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                threadFactory,
                rejectionHandler
        );
    }
    
    private void shutdownExecutor(ExecutorService executor, String name) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.warn("{} executor did not terminate gracefully, forcing shutdown", name);
                executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    logger.error("{} executor did not terminate", name);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted while shutting down {} executor", name, e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public ResponseEntity<?> pic(MultipartFile image, int sqid, int cam, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            jwtUserDetailsService.authenticate(request, response, STUDENT);
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>()
                    .responseFail(localeService.getMessage("auth.forbidden", request)));
        }
        String err;
        if (image != null && !image.isEmpty()) {

            try {
                File file = new File(Constants.SERVER_LOCAL_PATH.concat("quiz/" + cam + "/").concat(String.valueOf(sqid)));

                if (!file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.mkdir();
                    file.mkdirs();
                    logger.info("Directory created: " + file.getAbsolutePath());
                }
                String epochMilli = RandomStringUtils.randomAlphanumeric(10);
                String extension = FilenameUtils.getExtension(image.getOriginalFilename());
                Files.copy(image.getInputStream(), Paths.get(file.getAbsolutePath()).resolve(epochMilli
                        + "." + extension), StandardCopyOption.REPLACE_EXISTING);

                String path = String.format("%s.%s", epochMilli, extension);
                JobRunnerScheduleStarter.addImgEntry(new ExamPic(path, sqid, cam == 1));

                // Execute notification in separate thread pool to reduce response time
                if (cam == 1) {
                    camImageExecutor.execute(() -> {
                        try {
                            notificationJobService.quizImg(sqid, path);
                        } catch (Exception e) {
                            logger.error("Error processing cam image notification for sqid={}, path={}", sqid, path, e);
                        }
                    });
                } else {
                    screenImageExecutor.execute(() -> {
                        try {
                            notificationJobService.quizScr(sqid, path);
                        } catch (Exception e) {
                            logger.error("Error processing screen image notification for sqid={}, path={}", sqid, path, e);
                        }
                    });
                }
                return ResponseEntity.ok().body(new ResponseWrapper<>().responseOk(true));
            } catch (IOException e) {
                logger.error("Error occurring while saving the profile image.", e);
                err = e.getMessage();
            }
        } else {
            err = "Image empty";
        }
        logger.error(err);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>().responseFail(err));
    }

    /**
     * Add audit log entry to AUDIT_LIST
     *
     * @param requestBody Audit log request containing qr, sqid, and description
     * @param request     HTTP request
     * @param response    HTTP response
     * @return ResponseEntity with success or error message
     */
    public ResponseEntity<?> addAuditLog(AuditLogRequest requestBody, HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtUserDetailsService.authenticate(request, response, STUDENT, VLE);
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>()
                    .responseFail(localeService.getMessage("auth.forbidden", request)));
        }

        try {
            // Validate description is provided
            if (requestBody.getDescription() == null || requestBody.getDescription().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>()
                        .responseFail("Description is required"));
            }

            // Create audit entry
            ExamPreflightAudit audit = new ExamPreflightAudit(
                    requestBody.getQr(),
                    requestBody.getSqid(),
                    requestBody.getDescription()
            );

            // Add to AUDIT_LIST
            JobRunnerScheduleStarter.addAuditEntry(audit);

            logger.info("Audit log added via API | QR: {} | SQID: {} | Description: {}",
                    requestBody.getQr() != null ? requestBody.getQr() : "N/A",
                    requestBody.getSqid() != null ? requestBody.getSqid() : "N/A",
                    requestBody.getDescription());

            return ResponseEntity.ok().body(new ResponseWrapper<>().responseOk(true));
        } catch (Exception e) {
            logger.error("Error adding audit log via API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>()
                    .responseFail("Error adding audit log: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> addAuditLogs(AuditLogRequest requestBody, HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtUserDetailsService.authenticate(request, response, ADMIN, CRM_ADMIN, CRM_USER, COORDINATOR,
                    COORDINATOR_USER, LECTURER, FINANCE);
        } catch (AuthorizationException e) {
            logger.error("Auth Error", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>()
                    .responseFail(localeService.getMessage("auth.forbidden", request)));
        }

        List<ExamPreflightAudit> bySqid = examPreflightAuditRepository.findBySqid(requestBody.getQr() == null ? "" : requestBody.getQr(),
                requestBody.getSqid() == null ? -1 : requestBody.getSqid());
        return ResponseEntity.ok().body(new ResponseWrapper<>().responseOk(bySqid));
    }

    /**
     * Get images by sqid - returns two lists: cam=true images and cam=false images
     *
     * @param sqid     Student Quiz ID
     * @param request  HTTP request
     * @param response HTTP response
     * @return ResponseEntity with two lists of images
     */
    public ResponseEntity<?> getImagesBySqid(Integer sqid, HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtUserDetailsService.authenticate(request, response, ADMIN, CRM_ADMIN, CRM_USER, COORDINATOR,
                    COORDINATOR_USER, LECTURER, FINANCE, STUDENT, VLE);
        } catch (AuthorizationException e) {
            logger.error("Auth Error", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>()
                    .responseFail(localeService.getMessage("auth.forbidden", request)));
        }

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            // Fetch all images from database for the given sqid
            List<ExamPic> examPics = studentQuizRepository.findBySqId(sqid);

            // List for cam=true images
            List<Map<String, Object>> camTrueImages = new ArrayList<>();
            // List for cam=false images
            List<Map<String, Object>> camFalseImages = new ArrayList<>();

            // Segregate images by cam field
            for (ExamPic examPic : examPics) {
                String name = examPic.getImg(); // e.g., 1749899011479.webp
                String url = Constants.SEVER_BASE.concat("quiz/")
                        .concat(examPic.isCam()?"1":"0")
                        .concat("/")
                        .concat(String.valueOf(sqid))
                        .concat("/")
                        .concat(name);

                Map<String, Object> item = new HashMap<>();
                item.put("name", simpleDateFormat.format(examPic.getAddedTime()));
                item.put("url", url);

                // Segregate based on cam field
                if (examPic.isCam()) {
                    camTrueImages.add(item);
                } else {
                    camFalseImages.add(item);
                }
            }

            // Create response map with both lists
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("cam", camTrueImages);
            responseData.put("scr", camFalseImages);

            logger.info("Retrieved images for SQID: {} | Cam=true: {} | Cam=false: {}",
                    sqid, camTrueImages.size(), camFalseImages.size());

            return ResponseEntity.ok().body(new ResponseWrapper<>().responseOk(responseData));
        } catch (Exception e) {
            logger.error("Error retrieving images for SQID: " + sqid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>()
                    .responseFail("Error retrieving images: " + e.getMessage()));
        }
    }
}
