package lk.mc.core.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.mc.core.exceptions.TsJSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Json Helper is used to converts classes to json objects/arrays. Set the common
 * json configurations such as date format, timezones, etc.. should be set in here.
 *
 * @author vihangawicks
 * @since 11/16/21
 * MC-lms
 */
public class JSONHelper {

    /**
     * Converts a single key value pair to a JsonNode {}
     *
     * @param key   string json key
     * @param value string json value
     * @return converted object
     */
    public static JSONObject toJsonNode(String key, String value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        return jsonObject;
    }

    /**
     * Converts an java class object to a JsonNode {}
     *
     * @param object the object to be converted
     * @return converted object
     * @throws TsJSONException on a json error
     */
    public static JSONObject toJsonNode(Object object) {

        ObjectMapper om = new ObjectMapper();

//        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"));
//        om.setTimeZone(TimeZone.getDefault());
        JsonNode n = om.convertValue(object, JsonNode.class);

        return new JSONObject(n.toString());

    }

    /**
     * Converts an util list to a JsonArray []
     *
     * @param list the list to converted
     * @return converted array
     */
    public static JSONArray toJsonArray(List list) {

        return new JSONArray(list);

    }

    /**
     * Return the object mapper. You can set the default/common properties of the object mapper here
     */
    public static ObjectMapper getDefaultObjectMapper() {
        return new ObjectMapper();
    }
}
