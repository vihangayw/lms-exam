package lk.mc.std.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "health_check")
@NoArgsConstructor
public class HealthCheck implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Colombo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkedAt;

    public HealthCheck(Date checkedAt) {
        this.checkedAt = checkedAt;
    }
}
