package lk.mc.internationalization.service;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface defines getters for locale internationalization based on http request headers.
 * Request headers should contain <code>Accept-Language</code> parameter.
 *
 * @author vihangawicks
 * @since 11/12/21
 * MC-lms
 */
public interface LocaleService {

    /**
     * Get the localized string value
     *
     * @param code    key value
     * @param request HttpServletRequest to capture header
     * @return translated value
     */
    String getMessage(String code, HttpServletRequest request);
}
