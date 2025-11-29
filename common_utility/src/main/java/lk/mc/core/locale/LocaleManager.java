package lk.mc.core.locale;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * LocaleManager will access the internationalization done
 * in the spring resources.
 * You can use <code>LocaleManager</code> go fetch messages and labels than need to
 * passed in responses and error messages
 *
 * @author vihangawicks
 * @since 11/12/21
 * MC-lms
 */
public class LocaleManager {

    private static LocaleManager instance;
    private static ResourceBundleMessageSource messageSource;

    /**
     * Get LocaleManager instance
     *
     * @return LocaleManager singleton
     */
    public static LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();

            messageSource = new ResourceBundleMessageSource();
            messageSource.setBasenames("locale/lang");
        }
        return instance;
    }

    /**
     * Translate a string value to a given language
     *
     * @param key    key value in lang.properties file
     * @param locale A <code>Locale</code> object
     * @return translated value if the locale file and key is defined. Return en value as default.
     */
    public String getValue(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

}
