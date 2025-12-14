package ru.yandex.practicum.telemetry.analyzer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        log.info("Обработка сообщения {} от {}", event, hubId);

        switch(event.getPayload()) {
            case DeviceAddedEventAvro deviceAddedEventAvro -> handleDeviceAddedEvent(hubId, deviceAddedEventAvro);
            case DeviceRemovedEventAvro deviceRemovedEventAvro -> handleDeviceRemovedEvent(hubId, deviceRemovedEventAvro);
            case ScenarioAddedEventAvro scenarioAddedEventAvro -> handleScenarioAddedEvent(hubId, scenarioAddedEventAvro);
            case ScenarioRemovedEventAvro scenarioRemovedEventAvro -> handleScenarioRemovedEvent(hubId, scenarioRemovedEventAvro);
            default -> log.warn("Получено событие неизвестного типа: {}", event);
        }
    }

    @Transactional
    private void handleDeviceAddedEvent(String hubId, DeviceAddedEventAvro event) {
        String deviceId = event.getId();
        sensorService.saveSensor(deviceId, hubId);
    }

    @Transactional
    private void handleDeviceRemovedEvent(String hubId, DeviceRemovedEventAvro event) {
        String deviceId = event.getId();
        sensorService.deleteSensor(deviceId, hubId);
    }

    @Transactional
    private void handleScenarioAddedEvent(String hubId, ScenarioAddedEventAvro event) {
        scenarioService.saveScenario(hubId, event);
    }

    @Transactional
    private void handleScenarioRemovedEvent(String hubId, ScenarioRemovedEventAvro event) {
        scenarioService.deleteScenario(hubId, event);
    }
}
