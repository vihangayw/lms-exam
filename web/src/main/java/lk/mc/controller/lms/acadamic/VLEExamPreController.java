package lk.mc.controller.lms.acadamic;

import lk.mc.service.lms.academic.ExamPreflightService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST Controller for user requests
 *
 * @author vihangawicks
 * @since 12/14/21
 * MC-lms
 */

@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/vle/pre-exam")
public class VLEExamPreController {

    private static final Logger logger = LogManager.getLogger(VLEExamPreController.class);

    private final ExamPreflightService service;

    @Autowired
    public VLEExamPreController(ExamPreflightService service) {
        this.service = service;
    }

    @PostMapping("/upload-desk")
    public ResponseEntity uploadDeskImage(MultipartFile deskImage, MultipartFile image360, String qr,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        logger.info(request.getRequestURI() + " | " + qr);
        return service.uploadImages(deskImage, image360, qr, request, response);
    }

    @PostMapping("/ack/{qr}")
    public ResponseEntity preFlight(@PathVariable String qr, HttpServletRequest request) {
        logger.info(request.getRequestURI());
        return service.preFlight(qr);
    }
}
