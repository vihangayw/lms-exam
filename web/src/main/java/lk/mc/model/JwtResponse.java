package lk.mc.model;

import java.io.Serializable;

/**
 * @author vihangawicks
 * @since 11/25/21
 * MC-lms
 */

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}