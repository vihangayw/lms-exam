package lk.mc.model;

import java.io.Serializable;

/**
 * Request model for reupload operation
 *
 * @author vihangawicks
 * @since 12/14/21
 * MC-lms
 */
public class ReuploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;
    private String qr;

    public ReuploadRequest() {
    }

    public ReuploadRequest(String userName, String qr) {
        this.userName = userName;
        this.qr = qr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }
}

