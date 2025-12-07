package ru.yandex.practicum.telemetry.analyzer.handler;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.entity.Condition;
import ru.yandex.practicum.telemetry.analyzer.entity.ConditionOperation;

import java.util.Map;
import java.util.Objects;

@UtilityClass
public final class ConditionAnalyzer {

    public boolean isSatisfiedConditions(SensorsSnapshotAvro snapshot, Map<String, Condition> conditions) {
        return conditions.entrySet().stream()
                .allMatch(entry -> {
                    String sensorId = entry.getKey();
                    Condition condition = entry.getValue();

                    SensorStateAvro state = snapshot.getSensorsState().get(sensorId);
                    if (state == null) return false;

                    SpecificRecordBase payload = (SpecificRecordBase) state.getData();
                    return checkValue(condition, payload);
                });
    }

    private boolean checkValue(Condition condition, SpecificRecordBase payload) {
        if (payload == null) return false;
        return switch (payload) {
            case ClimateSensorAvro data -> isTriggered(condition, data);
            case LightSensorAvro data -> isTriggered(condition, data);
            case MotionSensorAvro data -> isTriggered(condition, data);
            case SwitchSensorAvro data -> isTriggered(condition, data);
            case TemperatureSensorAvro data -> isTriggered(condition, data);
            default -> false;
        };
    }

    private static boolean isTriggered(Condition condition, ClimateSensorAvro data) {
        Object field = switch (condition.getType()) {
            case TEMPERATURE -> data.getTemperatureC();
            case CO2LEVEL -> data.getCo2Level();
            case HUMIDITY -> data.getHumidity();
            default -> null;
        };
        return matchesObject(field, condition.getValue(), condition.getOperation());
    }

    private static boolean isTriggered(Condition condition, LightSensorAvro data) {
        Object field = switch (condition.getType()) {
            case LUMINOSITY -> data.getLuminosity();
            default -> null;
        };
        return matchesObject(field, condition.getValue(), condition.getOperation());
    }

    private static boolean isTriggered(Condition condition, MotionSensorAvro data) {
        Object field = switch (condition.getType()) {
            case MOTION -> data.getMotion();
            default -> null;
        };
        return matchesObject(field, condition.getValue(), condition.getOperation());
    }

    private static boolean isTriggered(Condition condition, SwitchSensorAvro data) {
        Object field = switch (condition.getType()) {
            case SWITCH -> data.getState();
            default -> null;
        };
        return matchesObject(field, condition.getValue(), condition.getOperation());
    }

    private static boolean isTriggered(Condition condition, TemperatureSensorAvro data) {
        Object field = switch (condition.getType()) {
            case TEMPERATURE -> data.getTemperatureC();
            default -> null;
        };
        return matchesObject(field, condition.getValue(), condition.getOperation());
    }

    private static boolean matchesObject(Object field, Integer value, ConditionOperation operation) {
        if (field == null) return false;
        return switch (field) {
            case Integer i -> matchesInteger(i, value, operation);
            case Boolean b -> matchesBoolean(b, value, operation);
            default -> false;
        };
    }

    private static boolean matchesInteger(Integer v1, Integer v2, ConditionOperation operation) {
        return switch (operation) {
            case EQUALS -> Objects.equals(v1, v2);
            case GREATER_THAN -> v1 > v2;
            case LOWER_THAN -> v1 < v2;
            default -> false;
        };
    }

    private static boolean matchesBoolean(Boolean b1, Integer v2, ConditionOperation operation) {
        Boolean b2 = v2 != null && v2 > 0;
        return switch (operation) {
            case EQUALS -> b1.equals(b2);
            default -> false;
        };
    }
}
