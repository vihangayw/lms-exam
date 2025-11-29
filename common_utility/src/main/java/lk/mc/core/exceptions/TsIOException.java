package lk.mc.core.exceptions;

import java.io.IOException;

/**
 * In the case of Input/output data error this should be thrown. This will be mainly used
 * in json object handling and API responses.
 * Signals that an I/O exception of some sort has occurred. This class is the general
 * class of exceptions produced by failed or interrupted I/O operations.
 *
 * @author vihangawicks
 * @since 11/16/21
 * MC-lms
 */
public class TsIOException extends IOException {

    public TsIOException() {
        super();
    }

    public TsIOException(String message) {
        super(message);
    }

    public TsIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public TsIOException(Throwable cause) {
        super(cause);
    }
}
