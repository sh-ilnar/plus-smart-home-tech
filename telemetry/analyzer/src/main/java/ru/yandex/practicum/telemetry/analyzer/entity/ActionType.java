package ru.yandex.practicum.telemetry.analyzer.entity;

import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

public enum ActionType {
    ACTIVATE,
    DEACTIVATE,
    INVERSE,
    SET_VALUE;

    public static ActionType fromAvro(ActionTypeAvro actionTypeAvro) {
        if (actionTypeAvro == null) {
            throw new IllegalArgumentException("actionTypeAvro не заполнен");
        }
        return switch (actionTypeAvro) {
            case ACTIVATE -> ACTIVATE;
            case DEACTIVATE -> DEACTIVATE;
            case INVERSE -> INVERSE;
            case SET_VALUE -> SET_VALUE;
            default -> throw new IllegalArgumentException("Не известный тип: " + actionTypeAvro);
        };
    }
}
