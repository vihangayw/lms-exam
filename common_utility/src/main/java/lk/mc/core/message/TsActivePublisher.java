package lk.mc.core.message;

import lk.mc.core.api.util.JSONHelper;
import lk.mc.core.message.bean.MqttPublishInfo;
import lk.mc.core.util.TsStringUtils;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.BackgroundJob;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * All MQTT Dispatchers should extend this one. This contain the method com.ts.core to all the dispatching.
 *
 * @author vihangawicks
 * @since 01/07/22
 * MC-lms
 */
@Data
public class TsActivePublisher {
    private static Logger logger = LogManager.getLogger(TsActivePublisher.class);

    private MqttPublishInfo metaData;
    // attach from service class
    private MqttDispatchService mqttDispatchService;


    public TsActivePublisher(MqttPublishInfo metaData) {
        this.metaData = metaData;

        logger.info("New shipment arrived | Address >>> " + metaData);
    }

    public TsActivePublisher(MqttPublishInfo metaData, MqttDispatchService mqttDispatchService) {
        this.metaData = metaData;
        this.mqttDispatchService = mqttDispatchService;
    }

    /**
     * Publish single class entity as the payload
     *
     * @param payload bean class
     */
    public void publishMessage(Object payload) {
        if (payload == null) {
            logger.warn("Payload not attached");
            return;
        }
        if (payload instanceof Integer) {
            sendMessage(String.valueOf(payload));
        } else if (payload instanceof String) {
            sendMessage(payload.toString());
        } else {
            JSONObject payloadPack = JSONHelper.toJsonNode(payload);

            sendMessage(payloadPack.toString());
        }
    }

    /**
     * Publish single class entity to list of receivers
     *
     * @param payload   bean class
     * @param receivers topics
     */
    public void broadcastMessage(List<String> receivers, Object payload) {
        if (payload == null) {
            logger.warn("Payload not attached");
            return;
        }
        JSONObject payloadPack = JSONHelper.toJsonNode(payload);

        broadcastMessage(receivers, payloadPack.toString());
    }

    /**
     * Publish a list of entities to a single topic
     *
     * @param payloads list of beans
     */
    public void publishMessages(List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            logger.warn("Payloads not attached");
            return;
        }
        JSONArray payloadPacks = JSONHelper.toJsonArray(payloads);

        sendMessage(payloadPacks.toString());
    }

    /**
     * Publish a list of entities to list of receivers
     *
     * @param payloads  list of beans
     * @param receivers topics
     */
    public void broadcasthMessages(List<String> receivers, List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            logger.warn("Payloads not attached");
            return;
        }
        JSONArray payloadPacks = JSONHelper.toJsonArray(payloads);

        broadcastMessage(receivers, payloadPacks.toString());
    }

    public void sendMessage(String message) {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        logger.info("Dispatching payload | Message >>> " + message);
        publish(metaData.getTopic(), mqttMessage);
    }

    private void broadcastMessage(List<String> receivers, String message) {
        byte[] bytes = message.getBytes();
        logger.info("Dispatching payload | Message >>> " + message);
        for (String topic : receivers) {
            MqttMessage mqttMessage = new MqttMessage(bytes);
            publish(topic, mqttMessage);
        }

    }

    private void publish(String topic, MqttMessage mqttMessage) {
        mqttMessage.setQos(metaData.getQos());
        mqttMessage.setRetained(metaData.isRetained());
        logger.info("Shipment info >>> Topic['" + topic + "'], QOS[" + metaData.getQos() + "], Retained["
                + metaData.isRetained() + "], RunOnBackground[" + metaData.isRunOnBackground() + "]");

        if (metaData.isRunOnBackground()) {
            //enqueue the job
            JobId enqueue = BackgroundJob.enqueue(
                    () -> mqttDispatchService.execute(topic, mqttMessage));

            System.out.println(TsStringUtils.ANSI_CYAN + " JobId['" + enqueue + "']" + TsStringUtils.ANSI_RESET);
        } else {
            mqttDispatchService.execute(topic, mqttMessage);
        }
        logger.info("✓ Payload dispatched successfully");
    }

}
