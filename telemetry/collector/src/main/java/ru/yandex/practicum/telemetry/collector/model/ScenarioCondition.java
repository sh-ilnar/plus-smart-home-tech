package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ScenarioCondition {

    private String sensorId;

    private ConditionType type;

    private ConditionOperationType operation;

    private Integer value;
}
