package lk.mc.core.api.response.factory.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lk.mc.core.api.response.Ancestor;
import lk.mc.core.api.response.BooleanResponse;
import lk.mc.core.api.response.factory.AncestorsFactory;
import lk.mc.core.api.util.JSONHelper;
import org.json.JSONObject;

/**
 * Object parser for the {@link BooleanResponse}
 *
 * @author vihangawicks
 * @created 11/15/22
 * MC-lms - Queue
 */
public class AncestorBooleanResponseFactory implements AncestorsFactory {

    @Override
    public Ancestor parse(JSONObject response) throws JsonProcessingException {
        return JSONHelper.getDefaultObjectMapper().readValue(response.toString(), BooleanResponse.class);
    }
}
