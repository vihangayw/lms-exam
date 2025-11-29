package lk.mc.core.exceptions;

/**
 * Base Exception class for application specific exceptions
 *
 * @author vihanga
 * @since 28/10/2021
 * MC-lms
 */
public class TsActiveException extends Exception {

    public TsActiveException() {
        super();
    }

    public TsActiveException(Exception ex) {
        super(ex);
    }

    public TsActiveException(String message) {
        super(message);
    }

    public TsActiveException(String message, Exception ex) {
        super(message, ex);
    }
}
