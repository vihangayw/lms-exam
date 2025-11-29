package lk.mc.std.job.impl;

import lk.mc.core.message.MqttDispatchService;
import lk.mc.core.message.TsActivePublisher;
import lk.mc.core.message.bean.MqttPublishInfo;
import lk.mc.core.util.TsStringUtils;
import lk.mc.std.job.NotificationJobService;
import lk.mc.std.util.Constants;
import lk.mc.std.util.MQTTUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

/**
 * @author vihangawicks
 * @created 1/7/23
 * MC-lms - lms
 */
@Description(value = "Service layer that implements method for notification services.")
@Service
public class NotificationJobServiceManager implements NotificationJobService {
    private static Logger logger = LogManager.getLogger(NotificationJobServiceManager.class);

    private final MqttDispatchService mqttDispatchService;

    @Autowired
    public NotificationJobServiceManager(MqttDispatchService mqttDispatchService) {
        this.mqttDispatchService = mqttDispatchService;
    }

    @Override
    public void quizImg(int studentQuizId, String imgName) {
        logger.info(NOTIFICATION_BOT + " Quiz|Img|{}", studentQuizId);
        String url =
                Constants.SEVER_BASE.concat("quiz/").concat(String.valueOf(studentQuizId))
                        .concat("/").concat(imgName);
        MqttPublishInfo mqttPublishInfo = new MqttPublishInfo(
                MQTTUtils.TOPIC_QUIZ_IMG + studentQuizId);
        new TsActivePublisher(mqttPublishInfo, mqttDispatchService).publishMessage(url);
    }

    @Override
    public void preFlightScan(String qr) {
        logger.info(NOTIFICATION_BOT + " Quiz|pre-check-qr|{}",
                TsStringUtils.ANSI_BLUE + qr + TsStringUtils.ANSI_RESET);

        MqttPublishInfo mqttPublishInfo = new MqttPublishInfo(
                MQTTUtils.TOPIC_QUIZ_PRE_FLIGHT_SCAN + qr);
        new TsActivePublisher(mqttPublishInfo, mqttDispatchService).publishMessage("1");
    }

    @Override
    public void preFlightUpload(String qr) {
        logger.info(NOTIFICATION_BOT + " Quiz|pre-check-upload|{}",
                TsStringUtils.ANSI_BLUE + qr + TsStringUtils.ANSI_RESET);

        MqttPublishInfo mqttPublishInfo = new MqttPublishInfo(
                MQTTUtils.TOPIC_QUIZ_PRE_FLIGHT_UPLOAD + qr);
        new TsActivePublisher(mqttPublishInfo, mqttDispatchService).publishMessage("1");
    }

}
