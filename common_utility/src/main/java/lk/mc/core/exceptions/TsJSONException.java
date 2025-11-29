package lk.mc.core.exceptions;

import org.json.JSONException;

/**
 * In the case of malformed processing of a json this error is thrown.
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 *
 * @author vihangawicks
 * @since 11/16/21
 * MC-lms
 */
public class TsJSONException extends JSONException {

    public TsJSONException(String message) {
        super(message);
    }

    public TsJSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public TsJSONException(Throwable cause) {
        super(cause);
    }
}
