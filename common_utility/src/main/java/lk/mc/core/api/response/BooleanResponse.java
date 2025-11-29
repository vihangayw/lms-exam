package lk.mc.core.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Use this class when api returns boolean response
 *
 * @author vihangawicks
 * @created 11/14/22
 * MC-lms - Queue
 */
public class BooleanResponse extends Ancestor<Boolean> {

    public BooleanResponse(@JsonProperty("message") String message,
                           @JsonProperty("data") Boolean data) {
        super(message, data);
    }
}