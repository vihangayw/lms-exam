package lk.mc.core.email.config;

import lk.mc.core.security.EncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Setting up Application.properties file with configurations required for using mail SMTP server.
 *
 * @author vihangawicks
 * @since 14/07/21
 * MC-lms
 */
@Description(value = "Configuration class for e-mail sender.")
@Configuration
public class EmailConfiguration {

    private final Environment environment;

    @Autowired
    public EmailConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(environment.getProperty("spring.mail.host"));
        javaMailSender.setPort(Integer.valueOf(environment.getProperty("spring.mail.port")));
        javaMailSender.setUsername(environment.getProperty("spring.mail.username"));

        try {
            javaMailSender.setPassword(EncryptUtils.decrypt(environment.getProperty("spring.mail.password")));
        } catch (Exception e) {
            e.printStackTrace();
            javaMailSender.setPassword(environment.getProperty("spring.mail.password"));
        }

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        javaMailProperties.put("mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtp.auth"));
        javaMailProperties.put("mail.smtp.from", environment.getProperty("spring.mail.properties.mail.from.email"));
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.debug", environment.getProperty("spring.mail.properties.mail.debug"));
        javaMailProperties.put("mail.smtp.ssl.trust", "*");

        javaMailSender.setJavaMailProperties(javaMailProperties);
        return javaMailSender;
    }

}
