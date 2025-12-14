package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface EventService {
    void handleSensorEvent(SensorEventProto sensorEvent);
    void handleHubEvent(HubEventProto hubEvent);
}
