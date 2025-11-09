package ru.yandex.practicum.telemetry.collector.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.*;

public class SensorEventMapper {

    public static SpecificRecordBase mapToAvro(SensorEvent event) {
        SpecificRecordBase payload = switch (event) {
            case ClimateSensorEvent ev -> mapToAvro(ev);
            case LightSensorEvent ev -> mapToAvro(ev);
            case MotionSensorEvent ev -> mapToAvro(ev);
            case SwitchSensorEvent ev -> mapToAvro(ev);
            case TemperatureSensorEvent ev -> mapToAvro(ev);
            default ->
                    throw new IllegalArgumentException("Неподдерживаемый тип SensorEvent: " + event.getClass().getName());
        };

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public static ClimateSensorAvro mapToAvro(ClimateSensorEvent event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    public static LightSensorAvro mapToAvro(LightSensorEvent event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    public static MotionSensorAvro mapToAvro(MotionSensorEvent event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.isMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    public static SwitchSensorAvro mapToAvro(SwitchSensorEvent event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.isState())
                .build();
    }

    public static TemperatureSensorAvro mapToAvro(TemperatureSensorEvent event) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }
}
