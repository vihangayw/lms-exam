package lk.mc.internationalization.helper;

import lk.mc.core.constants.ApplicationConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * <code>LocaleResolver</code> process all the http requests to capture the request language
 * and return the <code>Locale</code> object.
 * Return the default locale of the serve if the <i>Accept-Language</i> is
 * <code>null</code> or <code>empty</code>
 *
 * @author vihangawicks
 * @since 11/12/21
 * MC-lms
 */
@Component
public class LocaleResolver extends AcceptHeaderLocaleResolver {

    private static Logger logger = LogManager.getLogger(LocaleResolver.class);

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String language = request.getHeader(ApplicationConstants.LOCALE_HEADER);
        Locale locale;
        if (language == null || language.isEmpty()) {
            locale = Locale.getDefault();
        } else {
            locale = new Locale(language);
        }
        logger.info("Accept-Language | " + language + " | Locale | " + locale);
        return locale;
    }

}

