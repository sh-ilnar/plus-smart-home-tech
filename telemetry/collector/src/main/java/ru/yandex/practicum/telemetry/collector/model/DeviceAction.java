package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class DeviceAction {

    private String sensorId;

    private DeviceActionType type;

    private Integer value;
}
