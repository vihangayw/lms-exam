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
@Table(name = "chat_message", indexes = {
        @Index(name = "idx_chat_sqid", columnList = "sqid")
})
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sqid", nullable = false)
    private Integer sqid;

    @Column(name = "message", length = 500, nullable = false)
    private String message;

    /**
     * true = sent by admin/administration, false = sent by student
     */
    @Column(name = "from_admin", nullable = false)
    private boolean fromAdmin;

    /**
     * Admin's user ID from LMS (null for student messages)
     */
    @Column(name = "sender_user_id", length = 100)
    private String senderUserId;

    /**
     * Admin's display name (null for student messages)
     */
    @Column(name = "sender_name", length = 200)
    private String senderName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Colombo")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "added_time")
    private Date addedTime;

    public ChatMessage(Integer sqid, String message, boolean fromAdmin, String senderUserId, String senderName) {
        this.sqid = sqid;
        this.message = message;
        this.fromAdmin = fromAdmin;
        this.senderUserId = senderUserId;
        this.senderName = senderName;
        this.addedTime = new Date();
    }
}
