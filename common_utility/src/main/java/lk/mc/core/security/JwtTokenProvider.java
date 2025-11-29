package lk.mc.core.security;


import io.jsonwebtoken.*;
import lk.mc.core.constants.ApplicationConstants;
import lk.mc.core.exceptions.AuthorizationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT token based authentication using Java and Spring
 * The content of the header should look like the following:
 * <p>
 * Authorization: Bearer token
 * Or
 * You can use authentication header params
 * {X-AUTH-TOKEN}
 * <p>
 * more info (https://jwt.io)
 *
 * @author vihangawicks
 * @since 11/10/21
 * MC-lms
 */
public class JwtTokenProvider {

    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
     * micro services environment, this key would be kept on a config-server.
     */
    private static final String secretKey = Base64.getEncoder()
            .encodeToString("313ZpaGFuZ2F3IiwiaWF0IjoxNTE2M"
                    .getBytes());
    private static final Logger logger = LogManager.getLogger(JwtTokenProvider.class);
    private static JwtTokenProvider instance;

    public static JwtTokenProvider getInstance() {
        if (instance == null) {
            instance = new JwtTokenProvider();
        }
        return instance;
    }

    /**
     * This function will create a jwt token based the signature provided. Default validity time will be set to 24h.
     * <p>
     * Notice that the claim names are only three characters long as JWT is meant to be compact.
     *
     * @param username name of the user to which the token is generated as subject of the token
     * @param claimMap given set of claims
     * @return generated token with roles and user name
     */
    public String createToken(String username, Map<String, Object> claimMap) {

        Claims claims = Jwts.claims().setSubject(username);
        logger.info("Token generating for subject >> " + username);
        if (claimMap != null) {
            for (String key : claimMap.keySet()) {
                claims.put(key, claimMap.get(key));
            }
        }

        // 24h -> h * min * sec * millis
        long validityInMilliseconds = 24 * 60 * 60 * 1000;
        return getToken(username, validityInMilliseconds + new Date().getTime(), claims);
    }

    /**
     * This function will create a jwt token based the signature provided.
     * <p>
     * Notice that the claim names are only three characters long as JWT is meant to be compact.
     *
     * @param username         name of the user to which the token is generated as subject of the token
     * @param claimMap         given set of claims
     * @param validityInMillis validity time period in milliseconds. (time to expire the token)
     * @return generated token with roles and user name
     */
    public String createToken(String username, Map<String, Object> claimMap, long validityInMillis) {

        Claims claims = Jwts.claims().setSubject(username);
        logger.info("Token generating for subject >> " + username + " | validity millis " + validityInMillis);
        if (claimMap != null) {
            for (String key : claimMap.keySet()) {
                claims.put(key, claimMap.get(key));
            }
        }

        return getToken(username, validityInMillis, claims);
    }

    private String getToken(String username, long validityInMillis, Claims claims) {

        Date validity = new Date(validityInMillis);
        logger.info("Token validate until " + validity);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer("lms")
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        logger.info("Token generated for username >> " + username);

        return token;
    }

    /**
     * Resolve request header to get the jwt token based on different header types: (Authorization, Header Params)
     *
     * @param req http request header
     * @return the jwt token or null if token not available
     */
    public String resolveToken(HttpServletRequest req) {
        String headerToken = req.getHeader(ApplicationConstants.AUTH_HEADER);
        if (headerToken != null && headerToken.startsWith(ApplicationConstants.AUTH_HEADER_PREFIX)) {
            logger.info("Bearer Token Authorization");
            return headerToken.substring(ApplicationConstants.AUTH_HEADER_PREFIX.length());
        }
        headerToken = req.getHeader(ApplicationConstants.AUTH_HEADER_KEY);
        if (headerToken != null && !headerToken.isEmpty()) {
            logger.info("X-AUTH-TOKEN Token");
            return headerToken;
        }
        logger.info("Token is null or empty");
        return null;
    }

    /**
     * Decrypt the jwt token to get the subject of the token
     *
     * @param token jwt token
     * @return the subject/username of the token
     */
    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Decode jwt token and return claims
     *
     * @param jwt token
     * @return claims
     */
    public Claims getClaims(String jwt) throws ExpiredJwtException {
        //This line will throw an exception if it is not a signed JWS (as expected)
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * Do a signature verification on a given token
     *
     * @param token jwt token that need to verify
     * @return true if the verification is success
     * @throws AuthorizationException on JwtException | IllegalArgumentException
     */
    public boolean validateToken(String token) throws AuthorizationException {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Signature verification failed. >> " + e.getMessage(), e);
            throw new AuthorizationException(e);
        }
    }

    /*uncomment for testing purposes only*/
//    public static void main(String[] args) {
//        List<AppUserRole> objects = new ArrayList<>();
//        objects.add(AppUserRole.ROLE_DEVICE);
//        objects.add(AppUserRole.ROLE_SUPER_ADMIN);
//        String ivon = new JwtTokenProvider().createToken("ivon", objects);
//        System.out.println(ivon);
//
//        System.out.println(new JwtTokenProvider().getUsername(ivon));
//        try {
//            System.out.println(new JwtTokenProvider().validateToken(ivon));
//        } catch (AuthorizationException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(new JwtTokenProvider().getClaims(ivon));
//    }
}
