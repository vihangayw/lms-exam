package lk.mc.service.lms.academic;

import lk.mc.core.api.response.ResponseWrapper;
import lk.mc.core.exceptions.AuthorizationException;
import lk.mc.internationalization.service.LocaleService;
import lk.mc.service.JwtUserDetailsService;
import lk.mc.std.bean.ExamPic;
import lk.mc.std.job.NotificationJobService;
import lk.mc.std.repository.StudentQuizRepository;
import lk.mc.std.util.Constants;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.scheduling.BackgroundJob;
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
import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static lk.mc.core.enums.JwtTypes.STUDENT;

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
    private final NotificationJobService notificationJobService;

    @Autowired
    public VLEQuizService(LocaleService localeService, JwtUserDetailsService jwtUserDetailsService, StudentQuizRepository studentQuizRepository, NotificationJobService notificationJobService) {
        this.localeService = localeService;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.studentQuizRepository = studentQuizRepository;
        this.notificationJobService = notificationJobService;
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
                File file = new File(Constants.SERVER_LOCAL_PATH.concat("quiz/").concat(String.valueOf(sqid)));

                if (!file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.mkdir();
                    file.mkdirs();
                    logger.info("Directory created: " + file.getAbsolutePath());
                }
                long epochMilli = Instant.now().toEpochMilli();
                String extension = FilenameUtils.getExtension(image.getOriginalFilename());
                Files.copy(image.getInputStream(), Paths.get(file.getAbsolutePath()).resolve(epochMilli
                        + "." + extension), StandardCopyOption.REPLACE_EXISTING);

                String path = String.format("%s.%s", epochMilli, extension);
                if (new SecureRandom().nextBoolean()) studentQuizRepository.save(new ExamPic(path, sqid, cam==1));

                BackgroundJob.enqueue(() -> notificationJobService.quizImg(sqid, path));
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

}
