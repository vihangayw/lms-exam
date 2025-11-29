package lk.mc.core.api.callback;

import lk.mc.core.api.response.Ancestor;

/**
 * Postman response listener allows to convert the postman response to
 * a well formatted structure.
 * If the API response return a success(200), it will convert the json
 * to ancestor and return the response along with the response.
 * <p>
 * If there is a connection issue or any other response returned from
 * the API, then it will trigger the error with a proper exception.
 *
 * @author vihangawicks
 * @since 11/16/21
 * MC-lms
 */
public interface PostmanResponseListener {

    /**
     * Should trigger this on the success response which is a http 200.
     *
     * @param ancestor     parsed value for ancestor factory class
     * @param responseCode http response code
     * @param response     original http response which returned from the lk.mc.feedback.api
     */
    void onResponse(Ancestor ancestor, int responseCode, String response);

    /**
     * Should trigger this on an error response.
     *
     * @param e thrown error
     */
    void onError(Exception e);

}
