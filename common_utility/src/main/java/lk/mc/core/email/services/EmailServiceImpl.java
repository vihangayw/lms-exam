package lk.mc.core.email.services;

import lk.mc.core.email.config.EmailConfiguration;
import lk.mc.core.email.interfaces.EmailService;
import lk.mc.core.email.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * For sending an email to the recipient’s email address, you need to autowire the JavaMailSender in the EmailServiceImpl class.
 * Declaration of methods which will be implemented by EmailServiceImpl class.
 * <p>
 * To send a more sophisticated email with an attachment, MimeMessage can be used.
 * MimeMessageHelper works as a helper class for MimeMessage to add the attachment and other details required to send the mail.
 *
 * @author vihangawicks
 * @since 14/07/21
 * MC-lms
 */
@Description(value = "Service layer that implements method for sending e-mails.")
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final EmailConfiguration emailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailServiceImpl(EmailConfiguration emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }


    @Override
    public Boolean sendSimpleMessage(Email email) {
        return send(email);
    }

    @Override
    public Boolean sendMessageWithCC(Email email) {
        return send(email);
    }

    private Boolean send(Email email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        String to = String.join(",", email.getRecipients());
        mailMessage.setTo(to);
        mailMessage.setText(email.getBody());
        mailMessage.setSubject(email.getSubject());

        boolean isSent = false;
        try {
            LOGGER.info("Sending e-mail >> {}", to);
            emailSender.getMailSender().send(mailMessage);
            LOGGER.info("Email sent to >> {}", to);
            isSent = true;
        } catch (Exception e) {
            LOGGER.error("Sending e-mail error", e);
        }
        return isSent;
    }

    @Override
    public Boolean sendMessageWithAttachment(Email email) throws IOException, MessagingException {
        MimeMessage message = emailSender.getMailSender().createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

        String to = String.join(",", email.getRecipients());
        messageHelper.setTo(to);
        messageHelper.setSubject(email.getSubject());
        messageHelper.setText(email.getBody(), email.getIsHtml());

        Resource resource = new ClassPathResource(email.getAttachmentPath());
        messageHelper.addAttachment("attachment", resource.getFile());

        boolean isSent = false;
        try {
            LOGGER.info("Sending e-mail with attachment >> {}", to);
            emailSender.getMailSender().send(message);
            LOGGER.info("Email sent to >> {}", to);
            isSent = true;
        } catch (Exception e) {
            LOGGER.error("Sending e-mail error", e);
        }
        return isSent;
    }

    @Override
    public Boolean sendHtmlMail(Email email) throws MessagingException {
        if (email.getTemplate() == null) {
            LOGGER.info("SendHtmlMail | Email template is null");
            return false;
        }

        // Prepare the evaluation context
        final Context ctx = new Context();
        String to = String.join(",", email.getRecipients());

        if (email.getContextVars() != null)
            email.getContextVars().keySet().forEach(var -> ctx.setVariable(var, email.getContextVars().get(var)));

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = emailSender.getMailSender().createMimeMessage();
        final MimeMessageHelper message
                = new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject(email.getSubject());
        message.setTo(to);
        if (email.getBccList() != null && !email.getBccList().isEmpty())
            message.setBcc(email.getBccList().toArray(new String[0]));
        if (email.getCcList() != null && !email.getCcList().isEmpty())
            message.setCc(email.getCcList().toArray(new String[0]));

        // Create the HTML body using Thymeleaf
        final String htmlContent = templateEngine.process(email.getTemplate().name(), ctx);
        message.setText(htmlContent, true /* isHtml */);

        // Add the attachments
        if (email.getAttachmentMap() != null)
            for (String name : email.getAttachmentMap().keySet())
                message.addAttachment(name, email.getAttachmentMap().get(name));

        // Send mail
        boolean isSent = false;
        try {
            LOGGER.info("Sending html e-mail with attachment >> {}", to);
            emailSender.getMailSender().send(mimeMessage);
            LOGGER.info("Email sent to >> {}", to);
            isSent = true;
        } catch (Exception e) {
            LOGGER.error("Sending e-mail error", e);
        }
        return isSent;

    }
}
