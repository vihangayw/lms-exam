package lk.mc.core.message;

import org.apache.logging.log4j.LogManager;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Service;

/**
 * Spring managed @Service bean to log the provided message.
 * And we can use @Job to define the job’s name (This will display in the JobRunr’s dashboard).
 *
 * @author vihangawicks
 * @since 01/07/22
 * MC-lms
 */
@Service
public class MqttDispatchService {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(MqttDispatchService.class);

    /**
     * Execute the mqtt job in the enqueue. Task will be executed through JobRunr.
     * Detail and summary of all task will be saved in the db. Accessible through the JobRunr dashboard (if enabled).
     *
     * @param topic       topic to send message
     * @param mqttMessage mqtt payload
     */
    @Job(name = "Mqtt Executor")
    public void execute(String topic, MqttMessage mqttMessage) {
        logger.info("Job received {}", topic);

        try {
            MqttManager.getInstance().publish(topic, mqttMessage);
        } catch (MqttException e) {
            logger.error("Publishing unsuccessful", e);
        }
    }

}
