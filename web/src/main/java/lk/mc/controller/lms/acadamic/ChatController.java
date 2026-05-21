package lk.mc.controller.lms.acadamic;

import lk.mc.model.AdminChatMessageRequest;
import lk.mc.model.ChatMessageRequest;
import lk.mc.service.lms.academic.ChatService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for live chat between students and administration.
 * <p>
 * Endpoints:
 * GET  /vle/chat/{sqid}         - load message history (student + admin)
 * POST /vle/chat/{sqid}         - student sends a message
 * POST /vle/chat/admin/{sqid}   - admin sends a message (triggers MQTT via BackgroundJob)
 *
 * @author vihangawicks
 */
@RestController
@RequestMapping("/vle/chat")
public class ChatController {

    private static final Logger logger = LogManager.getLogger(ChatController.class);

    private final ChatService service;

    @Autowired
    public ChatController(ChatService service) {
        this.service = service;
    }

    @GetMapping("/{sqid}")
    public ResponseEntity<?> loadMessages(@PathVariable Integer sqid,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        logger.info("{} | sqid={}", request.getRequestURI(), sqid);
        return service.loadMessages(sqid, request, response);
    }

    @PostMapping("/{sqid}")
    public ResponseEntity<?> sendStudentMessage(@PathVariable Integer sqid,
                                                @RequestBody ChatMessageRequest body,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        logger.info("{} | sqid={}", request.getRequestURI(), sqid);
        return service.sendStudentMessage(sqid, body, request, response);
    }

    @PostMapping("/admin/{sqid}")
    public ResponseEntity<?> sendAdminMessage(@PathVariable Integer sqid,
                                              @RequestBody AdminChatMessageRequest body,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        logger.info("{} | sqid={} | admin={}", request.getRequestURI(), sqid, body.getUserId());
        return service.sendAdminMessage(sqid, body, request, response);
    }
}
