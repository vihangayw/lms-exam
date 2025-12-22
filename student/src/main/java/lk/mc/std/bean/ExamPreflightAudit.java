package lk.mc.std.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "exam_audit", indexes = {
        @Index(name = "idx_audit_qr", columnList = "qr"),
        @Index(name = "idx_audit_sqid", columnList = "sqid")
})
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamPreflightAudit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "qr", length = 100)
    private String qr;

    @Column(name = "sqid")
    private Integer sqid;

    @Column(name = "description", length = 500)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Colombo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedTime;

    public ExamPreflightAudit(String qr, Integer sqid, String description) {
        this.qr = qr;
        this.sqid = sqid;
        this.description = description;
        this.addedTime = new Date();
    }

    public ExamPreflightAudit(Integer id) {
        this.id = id;
    }
}

