package lk.mc.config;

import lk.mc.core.message.MqttManager;
import lk.mc.core.message.bean.MqttConf;
import lk.mc.core.security.EncryptUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures connectionfactory
 *
 * @author vihangawicks
 * @since 02/07/22
 * MC-lms
 */
@Configuration
public class ActiveMqConfiguration {
    private static Logger logger = LogManager.getLogger(ActiveMqConfiguration.class);

    @Value("${spring.activemq.broker-url}")
    private String broker;
    @Value("${spring.activemq.user}")
    private String userName;
    @Value("${spring.activemq.password}")
    private String password;

    /**
     * Initialize MQTT configurations by setting broker, user, password and other configs
     */
    @Bean
    public void initActiveMq() {
        String encrypt = "";
        try {
            encrypt = EncryptUtils.decrypt(password);
        } catch (Exception e) {
            logger.error("Decrypt password fail >> " + e.getMessage(), e);
        }

        MqttManager.createInstance(new MqttConf(broker, userName, encrypt));
    }

}