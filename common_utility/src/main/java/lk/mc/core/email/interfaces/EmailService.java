package lk.mc.core.email.interfaces;

import lk.mc.core.email.model.Email;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Declaration of methods which will be implemented by EmailServiceImpl class.
 *
 * @author vihangawicks
 * @since 14/07/21
 * MC-lms
 */
@Component
public interface EmailService {

    Boolean sendSimpleMessage(Email email);

    Boolean sendMessageWithCC(Email email);

    Boolean sendMessageWithAttachment(Email email) throws IOException, MessagingException;

    Boolean sendHtmlMail(Email email) throws MessagingException;

}
