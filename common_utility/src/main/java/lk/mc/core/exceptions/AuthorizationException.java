package lk.mc.core.exceptions;

/**
 * AuthorizationException is the exception class for identify unauthorized actions
 *
 * @author vihanga
 * @since 28/10/2021
 * MC-lms
 */
public class AuthorizationException extends TsActiveException {

    public AuthorizationException(String message, Exception ex) {
        super(message, ex);
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Exception ex) {
        super(ex);
    }
}
