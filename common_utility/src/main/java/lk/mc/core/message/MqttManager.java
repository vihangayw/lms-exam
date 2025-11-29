package lk.mc.core.message;

import lk.mc.core.message.bean.MqttConf;
import lk.mc.core.util.TsStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;
import java.util.UUID;

/**
 * This class is used to create the MQTT connection with the server. This is a singleton class which will
 * return an instance of {@link IMqttClient}.
 *
 * @author vihangawicks
 * @since 1/24/22
 * MC-lms
 */
public class MqttManager {

    private static IMqttClient instance;
    private static MqttManager manager;
    private static Logger logger = LogManager.getLogger(MqttManager.class);

    private static MqttConf conf;

    /**
     * Initialize the instance by setting mqtt configs
     *
     * @param conf - mqtt config
     */
    public static void createInstance(MqttConf conf) {
        MqttManager.conf = conf;

        getInstance();
    }

    public static MqttManager getMangerInstance() {
        return manager != null ? manager : new MqttManager();
    }

    /**
     * Get mqtt instance for any application
     *
     * @return {@link IMqttClient}
     */
    public static IMqttClient getInstance() {
        if (instance == null) {
            try {
                manager = new MqttManager();
                logger.info("Initializing the mosquito broker...");
                instance = new MqttClient(conf.getBroker(), UUID.randomUUID().toString(), new MemoryPersistence());

                instance.setCallback(new MqttCallback() {

                    @Override
                    public void connectionLost(Throwable t) {
                        if (t != null) {
                            logger.warn("MQTT Connection Lost: " + t.getMessage(), t.getLocalizedMessage());
                            t.printStackTrace();
                        } else
                            logger.warn("MQTT Connection Lost: Cause null");
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage mm) {
                        logger.info("MQTT Message Received: " + topic);
                        logger.info("MQTT Message: " + Arrays.toString(mm.getPayload()));
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        logger.info("Shipment Delivered | " + token.isComplete());
                    }
                });

            } catch (MqttException e) {
                logger.error("MQTT Error Occurred !", e);
            }
        }
        try {
            connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * Set {@link MqttConnectOptions} and connect to broker if the connection is broken
     *
     * @throws MqttException when the broker is unavailable
     */
    private static void connect() throws MqttException {
        logger.info("Broker connection > " + instance.isConnected());
        if (!instance.isConnected()) {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setMaxInflight(50); //please increase this value in a high traffic environment
            options.setConnectionTimeout(10);

            if (!TsStringUtils.isNullOrEmpty(conf.getUser()))
                options.setUserName(conf.getUser());
            if (!TsStringUtils.isNullOrEmpty(conf.getPassword()))
                options.setPassword(conf.getPassword().toCharArray());

            logger.info("Connecting to broker..." + options.toString());
            instance.connect(options);
            logger.info("MQTT connection success !");
        }
    }

    public void disconnect() {
        if (instance == null) return;
        logger.info("Broker connection > " + instance.isConnected());
        if (instance.isConnected()) {
            try {
                logger.info("Disconnecting broker...");
                instance.disconnect();
                logger.info("MQTT disconnected !");
            } catch (MqttException e) {
                logger.error("MQTT disconnect failed !", e);
            }
        }
    }


    // for testing purposes
//    public static void main(String[] args) throws MqttException {
//        instance = null;
//
//        Mqtt.getInstance().subscribe("test-topic-2223",2, (topic, message) -> {
//            System.out.println("y me");
//            System.out.println(topic);
//            System.out.println(message.toString());
////            Mqtt.getInstance().unsubscribe("test-topic-222");
//        });
//
//
//        MqttPublishInfo messagePublishModel = new MqttPublishInfo("test-topic-2223", false, 2);
//        MqttMessage mqttMessage = new MqttMessage(new Date().toString().getBytes());
//        mqttMessage.setQos(0);
//        mqttMessage.setRetained(!messagePublishModel.getRetained());
//
//        MqttManager.getInstance().publish(messagePublishModel.getTopic(), mqttMessage);
//        System.out.println("published");
//
//        new Thread(new Runnable() {
//            @SneakyThrows
//            @Override
//            public void run() {
//                Thread.sleep(2000);
//                try {
//                    Mqtt.getInstance().unsubscribe("test-topic-2223");
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("unsubed");
//            }
//        }).start();
//
//    }

}
