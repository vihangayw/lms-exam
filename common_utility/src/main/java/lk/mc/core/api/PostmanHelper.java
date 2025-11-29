package lk.mc.core.api;

import lk.mc.core.api.callback.PostmanResponseListener;
import lk.mc.core.api.response.factory.AncestorsFactory;
import lk.mc.core.exceptions.TsIOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This helper is used to send API requests to 3rd party web services.
 * All API call must go through this class.
 * Use singleton instance.
 *
 * @author vihangawicks
 * @since 11/16/21
 * MC-lms
 */
@SuppressWarnings("Duplicates")
public class PostmanHelper {

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PATCH = "PATCH";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    private static Logger logger = LogManager.getLogger(PostmanHelper.class);
    private static PostmanHelper instance;

    private PostmanHelper() {
    }

    public static PostmanHelper getInstance() {
        if (instance == null) {
            instance = new PostmanHelper();
        }
        return instance;
    }

    /**
     * Check connection to the given url
     *
     * @param url http url
     * @return whether a connection is available or not
     */
    private static boolean hasConnection(String url) {
        logger.info("Checking URL connection | " + url);
        try {
            boolean hasCon = ((HttpURLConnection) new URL(url).openConnection()).getResponseCode() == 200;
            logger.info("Result URL connection | " + hasCon);

            return hasCon;
        } catch (IOException e) {
            logger.error("API connection issue. >> " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Send application/json web requests
     * POST/GET with headers
     *
     * @param context           Postman Response Listener object
     * @param factory           ancestor factory implementation
     * @param httpMethod        GET/POST
     * @param apiUrl            http url
     * @param requestBodyString json array/object request body as string for POST
     * @param formatResponse    is the response should be formatted as ancestor or not
     */
    public void sendJsonObjectRequest(final PostmanResponseListener context, final AncestorsFactory factory,
                                      String httpMethod, String apiUrl, String requestBodyString,
                                      boolean formatResponse) {
        try {
            URL obj = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod(httpMethod);
            con.setConnectTimeout(20000);

            logger.info("Sending request to URL >>> " + apiUrl);
            //add request body for post
            if (httpMethod.equals(HTTP_METHOD_POST)) {
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(requestBodyString);
                wr.flush();
                logger.info("Request Body | " + requestBodyString);
            }

            //add request header
            /*con.setRequestProperty("User-Agent", USER_AGENT);*/

            int responseCode = con.getResponseCode();
            logger.info("Response Code | " + responseCode);
            StringBuilder response = new StringBuilder();
            if (responseCode > 299) errorBuilder(con, response);
            else responseBuilder(con, response);

            logger.info("Postman response | " + response.toString());
            if (formatResponse)
                context.onResponse(factory.parse(new JSONObject("{\"data\":" + response.toString() + "}")),
                        responseCode,
                        response.toString());
            else
                context.onResponse(factory.parse(new JSONObject(response.toString())), responseCode, response.toString());


        } catch (JSONException | IOException ex) {
            logger.error("Error in API >> ", ex);
            context.onError(ex);
            if (!hasConnection(apiUrl)) {
                logger.error("Connection issue | " + apiUrl);
            }
        } catch (NullPointerException ex) {
            logger.error(ex);
            context.onError(ex);
        }
    }

    private static void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public void sendJsonObjectRequestWithBearer(final PostmanResponseListener context, final AncestorsFactory factory,
                                                String httpMethod, String apiUrl, String requestBodyString,
                                                boolean formatResponse, String token) {
        try {
            if (httpMethod.equalsIgnoreCase(HTTP_METHOD_PATCH)) {
                allowMethods(HTTP_METHOD_PATCH);
            }
            URL obj = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod(httpMethod);
            con.setConnectTimeout(20000);

            String authHeaderValue = "Bearer " + token;
            con.setRequestProperty("Authorization", authHeaderValue);

            logger.info("Sending request to URL >>> " + apiUrl);
            //add request body for post
            if (httpMethod.equals(HTTP_METHOD_POST) || httpMethod.equals(HTTP_METHOD_PATCH)) {
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(requestBodyString);
                wr.flush();
                logger.info("Request Body | " + requestBodyString);
            }

            //add request header
            /*con.setRequestProperty("User-Agent", USER_AGENT);*/

            int responseCode = con.getResponseCode();
            logger.info("Response Code | " + responseCode);
            StringBuilder response = new StringBuilder();
            if (responseCode > 299) errorBuilder(con, response);
            else responseBuilder(con, response);

            logger.info("Postman response | " + response.toString());
            if (formatResponse && factory != null && context != null)
                context.onResponse(factory.parse(new JSONObject("{\"data\":" + response.toString() + "}")),
                        responseCode,
                        response.toString());
            else if (factory != null && context != null)
                context.onResponse(factory.parse(new JSONObject(response.toString())), responseCode, response.toString());

        } catch (JSONException | IOException ex) {
            logger.error("Error in API >> ", ex);
            if (context != null) context.onError(ex);
            if (!hasConnection(apiUrl)) {
                logger.error("Connection issue | " + apiUrl);
            }
        } catch (NullPointerException ex) {
            logger.error(ex);
            if (context != null) context.onError(ex);
        }
    }

    public void sendJsonObjectRequestWithUsernamePasswordAuth(final PostmanResponseListener context, final AncestorsFactory factory,
                                                              String httpMethod, String apiUrl, String requestBodyString,
                                                              boolean formatResponse, String username, String password) {
        try {
            URL obj = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod(httpMethod);
            con.setConnectTimeout(20000);

            // Set basic authentication header
            String authString = username + ":" + password;
            String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
            String authHeaderValue = "Basic " + encodedAuthString;
            con.setRequestProperty("Authorization", authHeaderValue);

            logger.info("Sending request to URL >>> " + apiUrl);
            logger.info("Client Id >>> " + username);
            //add request body for post
            if (httpMethod.equals(HTTP_METHOD_POST)) {
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(requestBodyString);
                wr.flush();
                logger.info("Request Body | " + requestBodyString);
            }

            //add request header
            /*con.setRequestProperty("User-Agent", USER_AGENT);*/

            int responseCode = con.getResponseCode();
            logger.info("Response Code | " + responseCode);
            StringBuilder response = new StringBuilder();
            if (responseCode > 299) errorBuilder(con, response);
            else responseBuilder(con, response);

            logger.info("Postman response | " + response.toString());
            if (formatResponse)
                context.onResponse(factory.parse(new JSONObject("{\"data\":" + response.toString() + "}")),
                        responseCode,
                        response.toString());
            else
                context.onResponse(factory.parse(new JSONObject(response.toString())), responseCode, response.toString());


        } catch (JSONException | IOException ex) {
            logger.error("Error in API >> ", ex);
            context.onError(ex);
            if (!hasConnection(apiUrl)) {
                logger.error("Connection issue | " + apiUrl);
            }
        } catch (NullPointerException ex) {
            logger.error(ex);
            context.onError(ex);
        }
    }

    /**
     * Build a <code>StringBuilder</code> from te http response
     *
     * @param con      Http URL Connection
     * @param response String Builder object
     * @throws TsIOException IOException
     */
    private void responseBuilder(HttpURLConnection con, StringBuilder response) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader((con.getInputStream())));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void errorBuilder(HttpURLConnection con, StringBuilder response) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader((con.getErrorStream())));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
