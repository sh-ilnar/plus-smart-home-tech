package ru.yandex.practicum.telemetry.analyzer.entity;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

public enum ConditionType {
    MOTION,
    LUMINOSITY,
    SWITCH,
    TEMPERATURE,
    CO2LEVEL,
    HUMIDITY;

    public static ConditionType fromAvro(ConditionTypeAvro conditionTypeAvro) {
        if (conditionTypeAvro == null) {
            throw new IllegalArgumentException("conditionType не заполнен");
        }
        return switch (conditionTypeAvro) {
            case MOTION -> MOTION;
            case LUMINOSITY -> LUMINOSITY;
            case SWITCH -> SWITCH;
            case TEMPERATURE -> TEMPERATURE;
            case CO2LEVEL -> CO2LEVEL;
            case HUMIDITY -> HUMIDITY;
            default -> throw new IllegalArgumentException("Не известный тип: " + conditionTypeAvro);
        };
    }
}
