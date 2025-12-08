package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;

import java.util.List;

public interface ScenarioService {
    List<Scenario> getScenariosByHubId(String hubId);
    void saveScenario(String hubId, ScenarioAddedEventAvro event);
    void deleteScenario(String hubId, ScenarioRemovedEventAvro event);
}
