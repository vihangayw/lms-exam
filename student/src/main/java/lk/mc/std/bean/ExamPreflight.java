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
@Table(name = "exam_preflight", indexes = {
        @Index(name = "idx_qrcode", columnList = "qrcode")
})
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamPreflight implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String reference; //uuid
    private String deskImage; //id.jpg
    private String deskImageOriginalFileName; //original file name
    private String image360; //id_360.jpg
    private String image360OriginalFileName; //original file name

    @Column(name = "qrcode", unique = true)
    private String qrcode;
    private boolean reupload;
    private String admin;

    @Transient
    private String url360; //id_360.jpg

    @Transient
    private String urlDesk; //id.jpg

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Colombo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Colombo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    public ExamPreflight(Integer id) {
        this.id = id;
    }
}

