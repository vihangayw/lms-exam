package lk.mc.service;

import io.jsonwebtoken.Claims;
import lk.mc.core.constants.ApplicationConstants;
import lk.mc.core.enums.JwtTypes;
import lk.mc.core.exceptions.AuthorizationException;
import lk.mc.internationalization.service.LocaleService;
import lk.mc.security.JwtTokenUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;

import static lk.mc.core.constants.ApplicationConstants.JWT_PW;

/**
 * JWTUserDetailsService implements the Spring Security UserDetailsService interface.
 * It overrides the loadUserByUsername for fetching user details from the database using the username.
 * The Spring Security Authentication Manager calls this method for getting the user details from
 * the database when authenticating the user details provided by the user.
 *
 * @author vihangawicks
 * @since 11/25/21
 * MC-lms
 */
@SuppressWarnings("Duplicates")
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(JwtUserDetailsService.class);

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;
    @Autowired
    protected LocaleService localeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (Arrays.stream(JwtTypes.values()).anyMatch(types -> username.equals(types.name()))) {
            return new User(username, JWT_PW, new ArrayList<>());
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }


    /**
     * If it has a valid JWT Token then this method can validate its authentication
     * {@link JwtTypes} can be set for one or more types
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param jwtTypes JwtTypes varargs
     * @return Claims jwt claims
     * @throws AuthorizationException forbidden request
     */
    public Claims authenticate(HttpServletRequest request, HttpServletResponse response, JwtTypes... jwtTypes)
            throws AuthorizationException {
        logger.debug("{} | Access Rights | {}", request.getRequestURI(), Arrays.toString(jwtTypes));
        String requestTokenHeader = request.getHeader(ApplicationConstants.AUTH_HEADER);

        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith(ApplicationConstants.AUTH_HEADER_PREFIX)) {
            jwtToken = requestTokenHeader.substring(ApplicationConstants.AUTH_HEADER_PREFIX.length());
        }
//        else {
//            logger.warn("JWT Token does not begin with Bearer String");
//        }

        // validation jwt types + expire date
        if (jwtTokenUtil.validateToken(jwtToken, jwtTypes)) {
            return jwtTokenUtil.getAllClaimsFromToken(jwtToken);
        }
        throw new AuthorizationException(localeService.getMessage("auth.forbidden", request));
    }

    public Claims authenticate(HttpServletRequest request, String jwtToken, JwtTypes... jwtTypes)
            throws AuthorizationException {
        logger.debug("{} | Access Rights | {}", request.getRequestURI(), Arrays.toString(jwtTypes));
//        String requestTokenHeader = request.getHeader(ApplicationConstants.AUTH_HEADER);

//
//        if (requestTokenHeader != null && requestTokenHeader.startsWith(ApplicationConstants.AUTH_HEADER_PREFIX)) {
//            jwtToken = requestTokenHeader.substring(ApplicationConstants.AUTH_HEADER_PREFIX.length());
//        }
//        else {
//            logger.warn("JWT Token does not begin with Bearer String");
//        }
        jwtToken = jwtToken.replace("Bearer ", "");
        // validation jwt types + expire date
        if (jwtTokenUtil.validateToken(jwtToken, jwtTypes)) {
            return jwtTokenUtil.getAllClaimsFromToken(jwtToken);
        }
        throw new AuthorizationException(localeService.getMessage("auth.forbidden", request));
    }
}
