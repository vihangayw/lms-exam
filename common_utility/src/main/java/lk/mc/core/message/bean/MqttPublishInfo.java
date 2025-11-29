package lk.mc.core.message.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Information required to send the mqtt message is passed through this bean.
 * (ex. topic, qos, etc...)
 * All the future implementation and customizations  go through this bean.
 *
 * @author vihanga
 * @since 02/07/22
 * MC-lms
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MqttPublishInfo {

    @NotNull
    @Size(min = 1, max = 255)
    private String topic;

    @NotNull
    private boolean retained;

    @NotNull
    private Integer qos = 2;

    private boolean runOnBackground;

    public MqttPublishInfo(@NotNull @Size(min = 1, max = 255) String topic) {
        this.topic = topic;
    }

    public MqttPublishInfo(boolean runOnBackground) {
        this.runOnBackground = runOnBackground;
    }

    public MqttPublishInfo(String topic, boolean runOnBackground) {
        this.topic = topic;
        this.runOnBackground = runOnBackground;
    }

    @Override
    public String toString() {
        return "MqttPublishInfo{" +
                "topic='" + topic + '\'' +
                '}';
    }
}