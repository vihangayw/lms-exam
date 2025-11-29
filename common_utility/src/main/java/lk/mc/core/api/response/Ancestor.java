package lk.mc.core.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Generic type class that serialize or de-serialize any json request that goes/received
 * by the application
 *
 * @author vihangawicks
 * @since 11/16/21
 * MC-lms
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class Ancestor<T> {

    private String message;
    private T data;

    @JsonCreator
    Ancestor(@JsonProperty("data") T data) {
        this.data = data;
    }
}
