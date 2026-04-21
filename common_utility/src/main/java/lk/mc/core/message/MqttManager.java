package lk.mc.core.message;

import lk.mc.core.message.bean.MqttConf;
import lk.mc.core.util.TsStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
    private static final Logger logger = LogManager.getLogger(MqttManager.class);

    private static MqttConf conf;

    /**
     * Initialize the instance by setting mqtt configs
     *
     *
      */
    public static void createInstance(MqttConf conf) {
        MqttManager.conf = conf;

        getInstance();
    }

    public static MqttManager getMangerInstance() {
        return manager != null ? manager : new MqttManager();
    }

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
                logger.error("MQTT Error Occurred !");
                e.printStackTrace();
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
        if (instance == null) return;
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

            if (!TsStringUtils.isNullOrEmpty(conf.getSsl())
                    && conf.getSsl().trim().equalsIgnoreCase("true")) {
                try {
                    sslConnection(options, conf.getCertPw());
                } catch (IOException | CertificateException | NoSuchAlgorithmException
                         | KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
                    e.printStackTrace();
                }
            }

            logger.info("Connecting to broker..." + options.toString());
            instance.connect(options);
            logger.info("MQTT connection success !");
        }
    }

    private static void sslConnection(MqttConnectOptions options, String pw) throws IOException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        if (pw == null) pw = "";
        // Load CA certificate
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = (X509Certificate) certificateFactory.generateCertificate(
                new FileInputStream(conf.getCa()));

        // Create TrustManager with the CA certificate
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("caCert", caCert);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        // Load client certificate and key
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(conf.getCert()), pw.toCharArray());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, pw.toCharArray());
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        // Create SSL context with the TrustManager and KeyManager
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagers, trustManagers, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        options.setSocketFactory(sslSocketFactory);
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
