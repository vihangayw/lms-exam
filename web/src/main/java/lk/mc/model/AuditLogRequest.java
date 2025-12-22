package lk.mc.model;

import java.io.Serializable;

/**
 * Request model for audit log operation
 *
 * @author vihangawicks
 * @since 12/14/21
 * MC-lms
 */
public class AuditLogRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String qr;
    private Integer sqid;
    private String description;

    public AuditLogRequest() {
    }

    public AuditLogRequest(String qr, Integer sqid, String description) {
        this.qr = qr;
        this.sqid = sqid;
        this.description = description;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public Integer getSqid() {
        return sqid;
    }

    public void setSqid(Integer sqid) {
        this.sqid = sqid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

