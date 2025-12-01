package lk.mc.controller.lms.acadamic;

import lk.mc.model.AuditLogRequest;
import lk.mc.service.lms.academic.VLEQuizService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/vle/quiz")
public class VLEQuizController {

    private static final Logger logger = LogManager.getLogger(VLEQuizController.class);

    private final VLEQuizService service;

    @Autowired
    public VLEQuizController(VLEQuizService service ) {
        this.service = service;
    }

    @PostMapping("/pic")
    public ResponseEntity<?> pic(MultipartFile image, Integer sqid, Integer cam, HttpServletRequest request,
                                 HttpServletResponse response) {
        logger.info(request.getRequestURI());
        return service.pic(image, sqid, cam, request, response);
    }

    @PostMapping("/audit-log")
    public ResponseEntity<?> addAuditLog(@RequestBody AuditLogRequest requestBody,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        logger.info(request.getRequestURI() + " | QR: " +
                (requestBody.getQr() != null ? requestBody.getQr() : "N/A") +
                " | SQID: " +
                (requestBody.getSqid() != null ? requestBody.getSqid() : "N/A"));
        return service.addAuditLog(requestBody, request, response);
    }

    @PostMapping("/audit-logs")
    public ResponseEntity<?> addAuditLogs(@RequestBody AuditLogRequest requestBody,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        logger.info(request.getRequestURI());
        return service.addAuditLogs(requestBody, request, response);
    }

    @GetMapping("/images/{sqid}")
    public ResponseEntity<?> getImagesBySqid(@PathVariable Integer sqid,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        logger.info(request.getRequestURI() + " | SQID: " + sqid);
        return service.getImagesBySqid(sqid, request, response);
    }
}
