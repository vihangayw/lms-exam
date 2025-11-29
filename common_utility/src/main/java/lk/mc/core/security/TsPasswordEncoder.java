package lk.mc.core.security;

import lk.mc.core.exceptions.TsActiveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This is a implementation of @link {@link PasswordEncoder} so the application can use
 * TSPasswordEncoder rather than using BCrypt.
 *
 * @author vihangawicks
 * @since 11/25/21
 * MC-lms
 */
public class TsPasswordEncoder implements PasswordEncoder {
    private static Logger logger = LogManager.getLogger(TsPasswordEncoder.class);

    private static TsPasswordEncoder instance;

    public static TsPasswordEncoder getInstance() {
        if (instance == null)
            instance = new TsPasswordEncoder();
        return instance;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        } else {
            try {
                return EncryptUtils.encrypt(rawPassword.toString());
            } catch (TsActiveException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        } else if (encodedPassword != null && encodedPassword.length() != 0) {
            String encrypt = "";
            try {
                encrypt = EncryptUtils.encrypt(rawPassword.toString());
            } catch (TsActiveException e) {
                logger.error(e.getMessage(), e);
            }
            return encrypt.equals(encodedPassword);
        } else {
            logger.warn("Empty encoded password");
            return false;
        }
    }
}
