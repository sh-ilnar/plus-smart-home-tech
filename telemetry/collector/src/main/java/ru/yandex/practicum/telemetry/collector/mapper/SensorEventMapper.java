package ru.yandex.practicum.telemetry.collector.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

public class SensorEventMapper {

    public static SpecificRecordBase mapToAvro(SensorEventProto event) {
        SpecificRecordBase payload = switch (event.getPayloadCase()) {
            case CLIMATE_SENSOR -> mapToAvro(event.getClimateSensor());
            case LIGHT_SENSOR -> mapToAvro(event.getLightSensor());
            case MOTION_SENSOR -> mapToAvro(event.getMotionSensor());
            case SWITCH_SENSOR -> mapToAvro(event.getSwitchSensor());
            case TEMPERATURE_SENSOR -> mapToAvro(event.getTemperatureSensor());
            default ->
                    throw new IllegalArgumentException("Неподдерживаемый тип SensorEvent: " + event.getClass().getName());
        };

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(
                        Instant.ofEpochSecond(
                                event.getTimestamp().getSeconds(),
                                event.getTimestamp().getNanos()
                        ))
                .setPayload(payload)
                .build();
    }

    public static ClimateSensorAvro mapToAvro(ClimateSensorProto event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    public static LightSensorAvro mapToAvro(LightSensorProto event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    public static MotionSensorAvro mapToAvro(MotionSensorProto event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.getMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    public static SwitchSensorAvro mapToAvro(SwitchSensorProto event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.getState())
                .build();
    }

    public static TemperatureSensorAvro mapToAvro(TemperatureSensorProto event) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }
}
