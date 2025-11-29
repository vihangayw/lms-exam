package lk.mc.core.api.response.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import lk.mc.core.api.response.Ancestor;
import org.json.JSONObject;

/**
 * Base Factory class for Ancestor, all the response classes should be implemented from the
 * AncestorsFactory
 *
 * @author vihangawicks
 * @since 11/16/21
 * MC-lms
 */
public interface AncestorsFactory {

    /**
     * Parse json object to transform it to a <code>Ancestor</code> type
     * Parsing response class must be implemented from <code>Ancestor</code>
     *
     * @param response http response
     * @return Ancestor tye object
     * @throws JsonProcessingException base error
     */
    Ancestor parse(JSONObject response) throws JsonProcessingException;
}
