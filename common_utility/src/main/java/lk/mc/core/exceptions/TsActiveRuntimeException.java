package lk.mc.core.exceptions;

/**
 * In the case of roll back any transaction this exception should thrown to the container. It will do the
 * rest of the transaction management part since container manage transaction use in this project
 *
 * @author vihanga
 * @since 28/10/2021
 * MC-lms
 */
public class TsActiveRuntimeException extends RuntimeException {

    public TsActiveRuntimeException() {
        super();
    }

    public TsActiveRuntimeException(Exception ex) {
        super(ex);
    }

    public TsActiveRuntimeException(String message) {
        super(message);
    }

    public TsActiveRuntimeException(String message, Exception ex) {
        super(message, ex);
    }
}
