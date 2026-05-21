package lk.mc.service.lms.academic;

import lk.mc.core.api.response.ResponseWrapper;
import lk.mc.core.exceptions.AuthorizationException;
import lk.mc.internationalization.service.LocaleService;
import lk.mc.model.AdminChatMessageRequest;
import lk.mc.model.ChatMessageRequest;
import lk.mc.service.JwtUserDetailsService;
import lk.mc.std.bean.ChatMessage;
import lk.mc.std.job.NotificationJobService;
import lk.mc.std.repository.ChatMessageRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import static lk.mc.core.enums.JwtTypes.*;

@Component
public class ChatService {

    private static final Logger logger = LogManager.getLogger(ChatService.class);
    private static final int MAX_STUDENT_MESSAGES = 50;
    private static final SimpleDateFormat SDF;

    static {
        SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SDF.setTimeZone(TimeZone.getTimeZone("Asia/Colombo"));
    }

    private final LocaleService localeService;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationJobService notificationJobService;

    @Autowired
    public ChatService(LocaleService localeService,
                       JwtUserDetailsService jwtUserDetailsService,
                       ChatMessageRepository chatMessageRepository,
                       NotificationJobService notificationJobService) {
        this.localeService = localeService;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.chatMessageRepository = chatMessageRepository;
        this.notificationJobService = notificationJobService;
    }

    /**
     * Load all chat messages for a sqid. Accessible by student and admin.
     */
    public ResponseEntity<?> loadMessages(Integer sqid, HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtUserDetailsService.authenticate(request, response, STUDENT, VLE, ADMIN);
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseWrapper<>().responseFail(localeService.getMessage("auth.forbidden", request)));
        }

        if (sqid == null || sqid <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper<>().responseFail("Invalid sqid"));
        }

        try {
            List<ChatMessage> messages = chatMessageRepository.findBySqidOrderByAddedTimeAsc(sqid);
            long studentMsgCount = chatMessageRepository.countStudentMessagesBySqid(sqid);
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("messages", messages);
            data.put("studentMessageCount", studentMsgCount);
            data.put("remainingMessages", Math.max(0, MAX_STUDENT_MESSAGES - studentMsgCount));
            return ResponseEntity.ok(new ResponseWrapper<>().responseOk(data));
        } catch (Exception e) {
            logger.error("Error loading chat messages for sqid={}", sqid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>().responseFail("Failed to load messages"));
        }
    }

    /**
     * Student sends a message. Enforces 50-message limit per sqid session.
     */
    public ResponseEntity<?> sendStudentMessage(Integer sqid, ChatMessageRequest body,
                                                HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtUserDetailsService.authenticate(request, response, STUDENT);
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseWrapper<>().responseFail(localeService.getMessage("auth.forbidden", request)));
        }

        if (sqid == null || sqid <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper<>().responseFail("Invalid sqid"));
        }
        if (body == null || body.getMessage() == null || body.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper<>().responseFail("Message cannot be empty"));
        }
        if (body.getMessage().length() > 250) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper<>().responseFail("Message too long (max 250 characters)"));
        }

        long studentMsgCount = chatMessageRepository.countStudentMessagesBySqid(sqid);
        if (studentMsgCount >= MAX_STUDENT_MESSAGES) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ResponseWrapper<>().responseFail("Message limit reached. Maximum 50 messages allowed per session."));
        }

        try {
            ChatMessage msg = new ChatMessage(sqid, body.getMessage().trim(), false, null, body.getSenderName());
            ChatMessage saved = chatMessageRepository.save(msg);
            logger.info("Chat message saved | sqid={} | id={}", sqid, saved.getId());
            final int sqidFinal = sqid;
            final String payload = buildMqttPayload(saved);
            BackgroundJob.enqueue(() -> notificationJobService.chatMessageAdmin(sqidFinal, payload));
            return ResponseEntity.ok(new ResponseWrapper<>().responseOk(saved));
        } catch (Exception e) {
            logger.error("Error saving student chat message for sqid={}", sqid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>().responseFail("Failed to send message"));
        }
    }

    /**
     * Admin sends a message to a student sqid. Saves to DB and dispatches
     * an MQTT notification via BackgroundJob so the student receives it in real time.
     */
    public ResponseEntity<?> sendAdminMessage(Integer sqid, AdminChatMessageRequest body,
                                              HttpServletRequest request, HttpServletResponse response) {
        try {
            jwtUserDetailsService.authenticate(request, response, VLE, ADMIN);
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseWrapper<>().responseFail(localeService.getMessage("auth.forbidden", request)));
        }

        if (sqid == null || sqid <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper<>().responseFail("Invalid sqid"));
        }
        if (body == null || body.getMessage() == null || body.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper<>().responseFail("Message cannot be empty"));
        }
        if (body.getMessage().length() > 500) {
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper<>().responseFail("Message too long (max 500 characters)"));
        }

        try {
            ChatMessage msg = new ChatMessage(
                    sqid,
                    body.getMessage().trim(),
                    true,
                    body.getUserId(),
                    body.getUserName()
            );
            ChatMessage saved = chatMessageRepository.save(msg);
            logger.info("Admin chat message saved | sqid={} | id={} | admin={}", sqid, saved.getId(), body.getUserId());

            // Dispatch MQTT notification to the student via LMS in background
            final int sqidFinal = sqid;
            final String payload = buildMqttPayload(saved);
            BackgroundJob.enqueue(() -> notificationJobService.chatMessage(sqidFinal, payload));

            return ResponseEntity.ok(new ResponseWrapper<>().responseOk(saved));
        } catch (Exception e) {
            logger.error("Error saving admin chat message for sqid={}", sqid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>().responseFail("Failed to send message"));
        }
    }

    /**
     * Builds the compact JSON payload published to the MQTT topic.
     */
    private String buildMqttPayload(ChatMessage msg) {
        String timeStr = msg.getAddedTime() != null ? SDF.format(msg.getAddedTime()) : "";
        String name = msg.getSenderName() != null ? escapeJson(msg.getSenderName()) : "Metropolitan College";
        return "{\"id\":" + msg.getId() +
                ",\"sqid\":" + msg.getSqid() +
                ",\"msg\":\"" + escapeJson(msg.getMessage()) + "\"" +
                ",\"name\":\"" + name + "\"" +
                ",\"admin\":" + msg.isFromAdmin() +
                ",\"time\":\"" + timeStr + "\"}";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
