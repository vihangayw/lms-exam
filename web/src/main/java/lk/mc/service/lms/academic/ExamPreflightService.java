package lk.mc.service.lms.academic;
 
import lk.mc.core.api.response.ResponseWrapper;
import lk.mc.core.exceptions.AuthorizationException;
import lk.mc.core.util.TsStringUtils;
import lk.mc.internationalization.service.LocaleService;
import lk.mc.service.JwtUserDetailsService;
import lk.mc.std.bean.ExamPreflight;
import lk.mc.std.job.NotificationJobService;
import lk.mc.std.repository.ExamPreflightRepository;
import lk.mc.std.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

import static lk.mc.core.enums.JwtTypes.VLE;

/**
 * Service for exam preflight operations.
 *
 * @author vihangawicks
 * @since 12/14/21
 * MC-lms
 */
@Component
public class ExamPreflightService {
    private static final Logger logger = LogManager.getLogger(ExamPreflightService.class);

    private final LocaleService localeService;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final ExamPreflightRepository examPreflightRepository;
    private final NotificationJobService notificationJobService;

    public ExamPreflightService(LocaleService localeService,
                                JwtUserDetailsService jwtUserDetailsService,
                                ExamPreflightRepository examPreflightRepository,
                                NotificationJobService notificationJobService) {
        this.localeService = localeService;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.examPreflightRepository = examPreflightRepository;
        this.notificationJobService = notificationJobService;
    }

    public ResponseEntity<?> uploadImages(MultipartFile deskImage, MultipartFile image360, String qrcode, HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtUserDetailsService.authenticate(request, response, VLE);
        } catch (AuthorizationException e) {
            logger.error("Auth Error", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>()
                    .responseFail(localeService.getMessage("auth.forbidden", request)));
        }

        // Validate at least one image is provided
        if ((deskImage == null || deskImage.isEmpty()) && (image360 == null || image360.isEmpty())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>()
                    .responseFail("At least one image (desk or 360) must be provided"));
        }

        String err;
        try {
            // Find or create ExamPreflight by qrcode
            ExamPreflight preflight = examPreflightRepository.findByQrcode(qrcode)
                    .orElseGet(() -> {
                        ExamPreflight newPreflight = new ExamPreflight();
                        newPreflight.setQrcode(qrcode);
                        newPreflight.setReference(UUID.randomUUID().toString());
                        newPreflight.setAddedTime(new Date());
                        newPreflight.setUpdatedTime(new Date());
                        return newPreflight;
                    });

            // Generate UUID reference if not already set
            if (TsStringUtils.isNullOrEmpty(preflight.getReference())) {
                preflight.setReference(UUID.randomUUID().toString());
            }

            // Save entity first to get ID if it's new
            if (preflight.getId() == null) {
                preflight = examPreflightRepository.save(preflight);
            }

            // Create directory using reference UUID
            File file = new File(Constants.SERVER_LOCAL_PATH
                    .concat("exam-preflight/")
                    .concat(preflight.getReference()));

            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdir();
                file.mkdirs();
                logger.info("Directory created: {}", file.getAbsolutePath());
            }

            // Upload desk image if provided and field is empty
            if (deskImage != null && !deskImage.isEmpty()) {
                if (TsStringUtils.isNullOrEmpty(preflight.getDeskImage())) {
                    String deskFileName = preflight.getId() + ".jpg";
                    File deskImageFile = new File(file.getAbsolutePath(), deskFileName);
                    Files.copy(deskImage.getInputStream(), deskImageFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);

                    logger.info("Exam preflight desk image saved > {}", deskImageFile.getAbsolutePath());

                    preflight.setDeskImage(deskFileName);
                    preflight.setDeskImageOriginalFileName(deskImage.getOriginalFilename());
                } else {
                    logger.info("Desk image already exists for qrcode: {}, skipping upload", qrcode);
                }
            }

            // Upload 360 image if provided and field is empty
            if (image360 != null && !image360.isEmpty()) {
                if (TsStringUtils.isNullOrEmpty(preflight.getImage360())) {
                    String image360FileName = preflight.getId() + "_360.jpg";
                    File image360File = new File(file.getAbsolutePath(), image360FileName);
                    Files.copy(image360.getInputStream(), image360File.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);

                    logger.info("Exam preflight 360 image saved > {}", image360File.getAbsolutePath());

                    preflight.setImage360(image360FileName);
                    preflight.setImage360OriginalFileName(image360.getOriginalFilename());
                } else {
                    logger.info("360 image already exists for qrcode: {}, skipping upload", qrcode);
                }
            }

            // Update preflight with file information
            preflight.setUpdatedTime(new Date());
            examPreflightRepository.save(preflight);

            // Send notification at the end
            notificationJobService.preFlightUpload(qrcode);
            return ResponseEntity.ok().body(new ResponseWrapper<>().responseOk(true));

        } catch (IOException e) {
            logger.error("Error occurring while saving the images.", e);
            err = e.getMessage();
        }

        logger.error(err);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseWrapper<>().responseFail(err));
    }


    public ResponseEntity preFlight(String qr) {
        notificationJobService.preFlightScan(qr);
        return ResponseEntity.ok().body(new ResponseWrapper<>().responseOk(true));
    }
}

