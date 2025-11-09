package ru.yandex.practicum.telemetry.collector.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.*;

public class HubEventMapper {

    public static SpecificRecordBase mapToAvro(HubEvent event) {
        SpecificRecordBase payload = switch (event) {
            case DeviceAddedEvent ev -> mapToAvro(ev);
            case DeviceRemovedEvent ev -> mapToAvro(ev);
            case ScenarioAddedEvent ev -> mapToAvro(ev);
            case ScenarioRemovedEvent ev -> mapToAvro(ev);
            default -> throw new IllegalArgumentException("Неподдерживаемый тип HubEvent: " + event.getClass().getName());
        };
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public static DeviceAddedEventAvro mapToAvro(DeviceAddedEvent event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(mapToAvro(event.getDeviceType()))
                .build();
    }

    public static DeviceRemovedEventAvro mapToAvro(DeviceRemovedEvent event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    public static ScenarioAddedEventAvro mapToAvro(ScenarioAddedEvent event) {
        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(event.getConditions().stream().map(HubEventMapper::mapToAvro).toList())
                .setAction(event.getActions().stream().map(HubEventMapper::mapToAvro).toList())
                .build();
    }

    public static ScenarioRemovedEventAvro mapToAvro(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    public static DeviceTypeAvro mapToAvro(DeviceType type) {
        return switch (type) {
            case DeviceType.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceType.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceType.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceType.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case DeviceType.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип DeviceType: " + type);
        };
    }

    public static ScenarioConditionAvro mapToAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapToAvro(condition.getType()))
                .setOperation(mapToAvro(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }

    public static ConditionTypeAvro mapToAvro(ConditionType type) {
        return switch (type) {
            case ConditionType.MOTION -> ConditionTypeAvro.MOTION;
            case ConditionType.LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case ConditionType.TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case ConditionType.CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case ConditionType.HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case ConditionType.SWITCH -> ConditionTypeAvro.SWITCH;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип ConditionType: " + type);
        };
    }

    public static ConditionOperationAvro mapToAvro(ConditionOperationType operation) {
        return switch (operation) {
            case ConditionOperationType.EQUALS -> ConditionOperationAvro.EQUALS;
            case ConditionOperationType.GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case ConditionOperationType.LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип ConditionOperationType: " + operation);
        };
    }

    public static DeviceActionAvro mapToAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapToAvro(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    public static ActionTypeAvro mapToAvro(DeviceActionType type) {
        return switch (type) {
            case DeviceActionType.ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DeviceActionType.DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case DeviceActionType.INVERSE -> ActionTypeAvro.INVERSE;
            case DeviceActionType.SET_VALUE -> ActionTypeAvro.SET_VALUE;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип DeviceActionType: " + type);
        };
    }
}
