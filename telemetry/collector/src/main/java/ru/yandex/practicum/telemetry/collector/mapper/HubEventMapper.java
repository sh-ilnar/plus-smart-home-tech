package ru.yandex.practicum.telemetry.collector.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

public class HubEventMapper {

    public static SpecificRecordBase mapToAvro(HubEventProto event) {
        SpecificRecordBase payload = switch (event.getPayloadCase()) {
            case DEVICE_ADDED -> mapToAvro(event.getDeviceAdded());
            case DEVICE_REMOVED -> mapToAvro(event.getDeviceRemoved());
            case SCENARIO_ADDED -> mapToAvro(event.getScenarioAdded());
            case SCENARIO_REMOVED -> mapToAvro(event.getScenarioRemoved());
            default -> throw new IllegalArgumentException("Неподдерживаемый тип HubEventProto: " + event.getClass().getName());
        };
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(
                        Instant.ofEpochSecond(
                                event.getTimestamp().getSeconds(),
                                event.getTimestamp().getNanos()
                        ))
                .setPayload(payload)
                .build();
    }

    public static DeviceAddedEventAvro mapToAvro(DeviceAddedEventProto event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(mapToAvro(event.getType()))
                .build();
    }

    public static DeviceRemovedEventAvro mapToAvro(DeviceRemovedEventProto event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    public static ScenarioAddedEventAvro mapToAvro(ScenarioAddedEventProto event) {
        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(event.getConditionList().stream().map(HubEventMapper::mapToAvro).toList())
                .setAction(event.getActionList().stream().map(HubEventMapper::mapToAvro).toList())
                .build();
    }

    public static ScenarioRemovedEventAvro mapToAvro(ScenarioRemovedEventProto event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    public static DeviceTypeAvro mapToAvro(DeviceTypeProto type) {
        return switch (type) {
            case DeviceTypeProto.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceTypeProto.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceTypeProto.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceTypeProto.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case DeviceTypeProto.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип DeviceTypeProto: " + type);
        };
    }

    public static ScenarioConditionAvro mapToAvro(ScenarioConditionProto condition) {
        Object value = switch (condition.getValueCase()) {
            case ScenarioConditionProto.ValueCase.BOOL_VALUE -> condition.getBoolValue();
            case ScenarioConditionProto.ValueCase.INT_VALUE -> condition.getIntValue();
            default -> throw new IllegalArgumentException("Неподдерживаемый тип ScenarioConditionProto value type: " + condition.getValueCase());
        };
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapToAvro(condition.getType()))
                .setOperation(mapToAvro(condition.getOperation()))
                .setValue(value)
                .build();
    }

    public static ConditionTypeAvro mapToAvro(ConditionTypeProto type) {
        return switch (type) {
            case ConditionTypeProto.MOTION -> ConditionTypeAvro.MOTION;
            case ConditionTypeProto.LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case ConditionTypeProto.TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case ConditionTypeProto.CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case ConditionTypeProto.HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case ConditionTypeProto.SWITCH -> ConditionTypeAvro.SWITCH;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип ConditionTypeProto: " + type);
        };
    }

    public static ConditionOperationAvro mapToAvro(ConditionOperationProto operation) {
        return switch (operation) {
            case ConditionOperationProto.EQUALS -> ConditionOperationAvro.EQUALS;
            case ConditionOperationProto.GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case ConditionOperationProto.LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип ConditionOperationProto: " + operation);
        };
    }

    public static DeviceActionAvro mapToAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapToAvro(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    public static ActionTypeAvro mapToAvro(ActionTypeProto type) {
        return switch (type) {
            case ActionTypeProto.ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case ActionTypeProto.DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case ActionTypeProto.INVERSE -> ActionTypeAvro.INVERSE;
            case ActionTypeProto.SET_VALUE -> ActionTypeAvro.SET_VALUE;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип ActionTypeProto: " + type);
        };
    }
}
