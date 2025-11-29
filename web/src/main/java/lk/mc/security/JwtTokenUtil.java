package lk.mc.security;

import io.jsonwebtoken.Claims;
import lk.mc.core.enums.JwtTypes;
import lk.mc.core.security.JwtTokenProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The JwtTokenUtil is responsible for performing JWT operations like creation and validation.
 * It makes use of the io.jsonwebtoken.Jwts for achieving this.
 *
 * @author vihangawicks
 * @since 11/25/21
 * MC-lms
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;
    private static final Logger logger = LogManager.getLogger(JwtTokenUtil.class);

    /**
     * retrieve username from jwt token
     *
     * @param token jwt token
     * @return subject
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * retrieve expiration date from jwt token
     *
     * @param token jwt token
     * @return subject
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * for retrieving any information from token we will need the secret key
     *
     * @param token jwt token
     * @return token claims
     */
    public Claims getAllClaimsFromToken(String token) {
        return JwtTokenProvider.getInstance().getClaims(token);
    }

    /**
     * check if the token has expired
     *
     * @param token jwt token
     * @return whether the token has expired
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * generate token for user
     *
     * @param userDetails basic user
     * @return jwt token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * generate tokens with expire date
     *
     * @param userDetails basic user
     * @param millis      valid time in millis
     * @param claims      jwt claims
     * @return jwt token
     */
    public String generateTokenWithExp(UserDetails userDetails, long millis, Map<String, Object> claims) {
        return JwtTokenProvider.getInstance().createToken(userDetails.getUsername(), claims, millis);
    }

    /**
     * while creating the token -
     * 1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
     * 2. Sign the JWT using the HS256 algorithm and secret key.
     * 3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
     * compaction of the JWT to a URL-safe string
     *
     * @param claims  claims
     * @param subject username/subject
     * @return jwt token
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return JwtTokenProvider.getInstance().createToken(subject, claims);
    }

    /**
     * validate token
     *
     * @param token       jwt token
     * @param userDetails basic user detail
     * @return whether the token is valid and subject is correct
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * validate token with jwt types
     *
     * @param token    jwt token
     * @param jwtTypes jwt token types
     * @return whether the token is valid and subject is correct
     */
    public Boolean validateToken(String token, JwtTypes[] jwtTypes) {
        try {
            final String username = getUsernameFromToken(token);

            if (Arrays.stream(jwtTypes).anyMatch(types -> username.equals(types.name()))) {
                return !isTokenExpired(token);
            }
        } catch (Exception e) {
            logger.error("Error Validating Token !", e);
        }
        return false;
    }
}