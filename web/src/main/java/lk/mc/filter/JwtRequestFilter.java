package lk.mc.filter;

import io.jsonwebtoken.ExpiredJwtException;
import lk.mc.core.constants.ApplicationConstants;
import lk.mc.security.JwtTokenUtil;
import lk.mc.service.JwtUserDetailsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The JwtRequestFilter extends the Spring Web Filter OncePerRequestFilter class.
 * For any incoming request this Filter class gets executed.
 * It checks if the request has a valid JWT token.
 * If it has a valid JWT Token then it sets the Authentication in the context, to specify that the current
 * request is authenticated.
 *
 * @author vihangawicks
 * @since 11/25/21
 * MC-lms
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static Logger logger = LogManager.getLogger(JwtRequestFilter.class);
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @SuppressWarnings("Duplicates")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader(ApplicationConstants.AUTH_HEADER);

        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith(ApplicationConstants.AUTH_HEADER_PREFIX)) {
            jwtToken = requestTokenHeader.substring(ApplicationConstants.AUTH_HEADER_PREFIX.length());
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token. >> " + e.getMessage(), e);
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired. >> " + e.getMessage(), e);
            }
        }
//        else {
//            logger.warn("JWT Token does not begin with Bearer String");
//        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // if token is valid configure Spring Security to manually set
            // authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

}