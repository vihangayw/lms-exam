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
@Table(name = "exam_pic")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamPic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer sqId;

    private boolean cam;

    @Column(length = 50)
    private String img;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Colombo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedTime;

    public ExamPic(String img, Integer sqId, boolean cam) {
        this.img = img;
        this.sqId = sqId;
        this.cam = cam;
        this.addedTime = new Date();
    }

    public ExamPic(Integer id) {
        this.id = id;
    }
}

