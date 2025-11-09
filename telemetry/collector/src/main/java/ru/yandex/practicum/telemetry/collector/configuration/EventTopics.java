package ru.yandex.practicum.telemetry.collector.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventTopics {

    @Value("${plus-smart-home-tech.kafka.sensor-event-topic}")
    private String sensorEventsTopic;

    @Value("${plus-smart-home-tech.kafka.hub-event-topic}")
    private String hubEventsTopic;

    public String getSensorEventsTopic() {
        return sensorEventsTopic;
    }

    public String getHubEventsTopic() {
        return hubEventsTopic;
    }
}
