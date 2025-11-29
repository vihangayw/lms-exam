package lk.mc.core.exceptions;

/**
 * @author vihanga
 * @since 28/10/2021
 * MC-lms
 */
public class OperationNotSupportException extends TsActiveException {
    public OperationNotSupportException(String message) {
        super(message);
    }

    public OperationNotSupportException(Exception ex) {
        super(ex);
    }
}
