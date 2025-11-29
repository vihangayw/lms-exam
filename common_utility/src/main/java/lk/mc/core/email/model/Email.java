package lk.mc.core.email.model;

import lk.mc.core.enums.EmailTemplate;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Description;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * EmailDetails class that contains fields such as recipient, msgBody, subject, and attachment.
 *
 * @author vihangawicks
 * @since 14/07/21
 * MC-lms
 */
@Description(value = "Class that represents Email transfer object.")
@Data
@NoArgsConstructor
public class Email {

    private List<String> recipients;
    private List<String> ccList;
    private List<String> bccList;
    private Map<String, File> attachmentMap;//name, file
    private Map<String, String> contextVars;//name, val
    private String subject;
    private String body;
    private Boolean isHtml;
    private String attachmentPath;
    private EmailTemplate template;

    @Override
    public String toString() {
        return "Email{" +
                "recipients=" + recipients +
                ", ccList=" + ccList +
                ", bccList=" + bccList +
                ", subject='" + subject + '\'' +
                ", template='" + template + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
