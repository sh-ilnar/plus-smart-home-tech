package ru.yandex.practicum.telemetry.analyzer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioService;
import ru.yandex.practicum.telemetry.analyzer.service.SensorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventHandler {

    private final SensorService sensorService;
    private final ScenarioService scenarioService;

    public void handleHubEvent(HubEventAvro event) {
        String hubId = event.getHubId();

        switch(event.getPayload()) {
            case DeviceAddedEventAvro deviceAddedEventAvro -> handleDeviceAddedEvent(hubId, deviceAddedEventAvro);
            case DeviceRemovedEventAvro deviceRemovedEventAvro -> handleDeviceRemovedEvent(hubId, deviceRemovedEventAvro);
            case ScenarioAddedEventAvro scenarioAddedEventAvro -> handleScenarioAddedEvent(hubId, scenarioAddedEventAvro);
            case ScenarioRemovedEventAvro scenarioRemovedEventAvro -> handleScenarioRemovedEvent(hubId, scenarioRemovedEventAvro);
            default -> log.warn("Получено событие неизвестного типа: {}", event);
        }
    }

    private void handleDeviceAddedEvent(String hubId, DeviceAddedEventAvro event) {
        String deviceId = event.getId();
        sensorService.saveSensor(deviceId, hubId);
    }

    private void handleDeviceRemovedEvent(String hubId, DeviceRemovedEventAvro event) {
        String deviceId = event.getId();
        sensorService.deleteSensor(deviceId, hubId);
    }

    private void handleScenarioAddedEvent(String hubId, ScenarioAddedEventAvro event) {
        scenarioService.saveScenario(hubId, event);
    }

    private void handleScenarioRemovedEvent(String hubId, ScenarioRemovedEventAvro event) {
        scenarioService.deleteScenario(hubId, event);
    }
}
