package lk.mc.core.email;

import lk.mc.core.email.interfaces.EmailService;
import lk.mc.core.email.model.Email;
import org.apache.logging.log4j.LogManager;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Spring managed @Service bean to log the provided message.
 * And we can use @Job to define the job’s name (This will display in the JobRunr’s dashboard).
 *
 * @author vihangawicks
 * @since 13/07/22
 * MC-lms
 */
@Service
public class EmailDispatchService {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(EmailDispatchService.class);

    private final EmailService emailService;

    @Autowired
    public EmailDispatchService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Job(name = "Email Sender")
    public void sendSimpleMessage(Email email) {
        logger.info("Email Sender {}", email);

        if (emailService.sendSimpleMessage(email)) logger.info("Email Sender: Job Complete");
    }

    @Job(name = "Email Sender With CC")
    public void sendMessageWithCC(Email email) {
        logger.info("Email Sender With CC {}", email);

        if (emailService.sendMessageWithCC(email)) logger.info("Email Sender With CC: Job Complete");
    }

    @Job(name = "Email Sender With Attachment")
    public void sendMessageWithAttachment(Email email) {
        logger.info("Email Sender With Attachment {}", email);

        try {
            if (emailService.sendMessageWithAttachment(email)) logger.info("Job Complete");
        } catch (IOException | MessagingException e) {
            logger.error("Email Sender With Attachment: Error sending attachment ", e);
        }
    }

    @Job(name = "Email Template Sender")
    public void sendHtmlMail(Email email) {
        logger.info("Email Template Sender {}", email);

        try {
            if (emailService.sendHtmlMail(email)) logger.info("Email Template Sender: Job Complete");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
