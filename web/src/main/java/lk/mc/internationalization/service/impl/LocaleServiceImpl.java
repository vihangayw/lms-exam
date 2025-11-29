package lk.mc.internationalization.service.impl;

import lk.mc.core.locale.LocaleManager;
import lk.mc.internationalization.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Locale service implementation, resolve the localization based on requests
 *
 * @author vihangawicks
 * @since 11/12/21
 * MC-lms
 */
@Service
public class LocaleServiceImpl implements LocaleService {

    @Autowired
    private LocaleResolver localeResolver;

    @Override
    public String getMessage(String code, HttpServletRequest request) {
        return LocaleManager.getInstance().getValue(code, localeResolver.resolveLocale(request));
    }

}
