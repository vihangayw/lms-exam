package lk.mc.std.job.impl;

import lk.mc.core.util.TsStringUtils;
import lk.mc.std.job.NotificationJobService;
import lk.mc.std.util.Constants;
import lk.mc.std.util.MQTTUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Description;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vihangawicks
 * @created 1/7/23
 * MC-lms - lms
 */
@Description(value = "Service layer that implements method for notification services.")
@Service
public class NotificationJobServiceManager implements NotificationJobService {
    private static final Logger logger = LogManager.getLogger(NotificationJobServiceManager.class);

     private static final String BEARER_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJWTEUiLCJuYW1lIjoiTE1TLU1DIiwiaWF0IjoxNjkxMzA2MDEwLCJhdXRob3IiOiJ2aWhhbmdhd2lja3MiLCJleHAiOjE5OTEzMDYwMTAsImlzcyI6Im1jOnZ5dzpqTWlGaWV6cjMxMyIsIm5iZiI6MTY5MTIwNTAwMH0.EAPlpsX1ZuoK5R_u4818-d4zJAIeXgXUKGqHu2x7SQM";
    
    // Lightweight RestTemplate with connection pooling for high traffic
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    @PostConstruct
    public void init() {
        // Configure RestTemplate with optimized settings for high traffic
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds
        factory.setReadTimeout(5000); // 5 seconds
        
        this.restTemplate = new RestTemplate(factory);
        
        // Pre-configure headers to avoid creating them on each request
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
        this.headers.setBearerAuth(BEARER_TOKEN);
    }
    
    /**
     * Sends MQTT message via POST request to the MQTT server
     * Note: This method is synchronous. Caller's thread handles async execution if needed.
     * VLEQuizService already uses thread pools (50-100 threads) for image notifications.
     * @param topic MQTT topic
     * @param message MQTT message payload
     */
    private void sendMqttRequest(String topic, String message) {
        try {
            // Create lightweight JSON payload
            Map<String, String> payload = new HashMap<>(2);
            payload.put("topic", topic);
            payload.put("message", message);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
            
            // POST request to MQTT server
            ResponseEntity<Boolean> response = restTemplate.postForEntity(
                    Constants.LMS_SEVER_BASE + "/mq/send",
                    request, 
                    Boolean.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK || 
                response.getStatusCode() == HttpStatus.CREATED) {
                Boolean success = response.getBody();
                if (success == null || !success) {
                    logger.warn("MQTT server returned false for topic: {} | message: {}", topic, message);
                }
            } else {
                logger.warn("MQTT request returned non-success HTTP status: {} for topic: {}", 
                        response.getStatusCodeValue(), topic);
            }
            
        } catch (Exception e) {
            logger.error("Failed to send MQTT request to topic: {} | error: {}", topic, e.getMessage());
        }
    }

    @Override
    public void quizImg(int studentQuizId, String imgName) {
        logger.info(NOTIFICATION_BOT + " Quiz|Img|{}", studentQuizId);
        String url =
                Constants.SEVER_BASE.concat("quiz/1/").concat(String.valueOf(studentQuizId))
                        .concat("/").concat(imgName);
        String topic = MQTTUtils.TOPIC_QUIZ_IMG + studentQuizId;
        sendMqttRequest(topic, url);
    }

    @Override
    public void quizScr(int studentQuizId, String imgName) {
        logger.info(NOTIFICATION_BOT + " Quiz|SCRN|{}", studentQuizId);
        String url =
                Constants.SEVER_BASE.concat("quiz/0/").concat(String.valueOf(studentQuizId))
                        .concat("/").concat(imgName);
        String topic = MQTTUtils.TOPIC_QUIZ_SCRN + studentQuizId;
        sendMqttRequest(topic, url);
    }

    @Override
    public void preFlightScan(String qr) {
        logger.info(NOTIFICATION_BOT + " Quiz|pre-check-qr|{}",
                TsStringUtils.ANSI_BLUE + qr + TsStringUtils.ANSI_RESET);

        String topic = MQTTUtils.TOPIC_QUIZ_PRE_FLIGHT_SCAN + qr;
        sendMqttRequest(topic, "1");
    }

    @Override
    public void preFlightUpload(String qr) {
        logger.info(NOTIFICATION_BOT + " Quiz|pre-check-upload|{}",
                TsStringUtils.ANSI_BLUE + qr + TsStringUtils.ANSI_RESET);

        String topic = MQTTUtils.TOPIC_QUIZ_PRE_FLIGHT_UPLOAD + qr;
        sendMqttRequest(topic, "1");
    }

    @Override
    public void chatMessage(int sqid, String messageJson) {
        logger.info(NOTIFICATION_BOT + " Quiz|chat|sqid={}", sqid);
        String topic = MQTTUtils.TOPIC_QUIZ_CHAT + sqid;
        sendMqttRequest(topic, messageJson);
    }

    @Override
    public void chatMessageAdmin(int sqid, String messageJson) {
        logger.info(NOTIFICATION_BOT + " Quiz|chat|admin|sqid={}", sqid);
        String topic = MQTTUtils.TOPIC_QUIZ_CHAT_ADMIN + sqid;
        sendMqttRequest(topic, messageJson);
    }

    @Override
    public boolean healthPing() {
        try {
            Map<String, String> payload = new HashMap<>(2);
            payload.put("topic", MQTTUtils.TOPIC_HEALTH);
            payload.put("message", "Exam Server | " + new Date());

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<Boolean> response = restTemplate.postForEntity(
                    Constants.LMS_SEVER_BASE + "/mq/send",
                    request,
                    Boolean.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                Boolean success = response.getBody();
                return success != null && success;
            }
            return false;
        } catch (Exception e) {
            logger.error("Health ping MQTT failed: {}", e.getMessage());
            return false;
        }
    }

}
