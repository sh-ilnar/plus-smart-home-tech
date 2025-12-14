package ru.yandex.practicum.telemetry.analyzer.entity;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

public enum ConditionOperation {
    EQUALS,
    GREATER_THAN,
    LOWER_THAN;

    public static ConditionOperation fromAvro(ConditionOperationAvro conditionOperationAvro) {
        if (conditionOperationAvro == null) {
            throw new IllegalArgumentException("ConditionOperation не заполнен");
        }
        return switch (conditionOperationAvro) {
            case EQUALS -> EQUALS;
            case GREATER_THAN -> GREATER_THAN;
            case LOWER_THAN -> LOWER_THAN;
            default -> throw new IllegalArgumentException("Не известный тип: " + conditionOperationAvro);
        };
    }
}
