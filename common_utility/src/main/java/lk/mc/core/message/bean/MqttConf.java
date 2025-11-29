package lk.mc.core.message.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Mqtt configurations. Initialize this bean on application start
 *
 * @author vihanga
 * @since 02/07/22
 * MC-lms
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MqttConf {

    @NotNull
    @Size(min = 1, max = 255)
    private String broker;
    private String user;
    private String password;


    @Override
    public String toString() {
        return "MqttConf{" +
                "broker='" + broker + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}