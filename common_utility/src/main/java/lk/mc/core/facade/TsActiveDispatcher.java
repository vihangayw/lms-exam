package lk.mc.core.facade;

import lk.mc.core.message.MqttDispatchService;
import lk.mc.core.message.bean.MqttPublishInfo;

/**
 * TsActiveDispatcher is the base interface for activemq sending facades
 *
 * @author vihanga
 * @since 02/07/22
 * MC-lms
 */
public interface TsActiveDispatcher {

    /**
     * Attach Mqtt message meta data like topic, qos, etc..
     *
     * @param publishInfo         Meta data for mqtt publish
     * @param mqttDispatchService JobRunr’s object
     */
    void attachShipment(MqttPublishInfo publishInfo, MqttDispatchService mqttDispatchService);
}
