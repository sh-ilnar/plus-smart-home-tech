package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.telemetry.collector.model.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.collector.service.EventService;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    @PostMapping("/sensors")
    public void postSensors(@Valid @RequestBody SensorEvent sensorEvent) {
        eventService.handleSensorEvent(sensorEvent);
    }

    @PostMapping("/hubs")
    public void postHubs(@Valid @RequestBody HubEvent hubEvent) {
        eventService.handleHubEvent(hubEvent);
    }
}
