package ru.yandex.practicum.telemetry.collector.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ScenarioRemovedEvent extends HubEvent {

    @Size(min = 3, max = Integer.MAX_VALUE)
    private String name;

    @Override
    public HubEventType getType() {return HubEventType.SCENARIO_REMOVED; }

}
