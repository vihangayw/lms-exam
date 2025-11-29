package lk.mc.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author vihangawicks
 * @since 1/24/22
 * MC-lms
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class TsMqttException extends RuntimeException {

    public TsMqttException(String message) {
        super(message);
    }

    public TsMqttException(Throwable cause) {
        super(cause);
    }
}