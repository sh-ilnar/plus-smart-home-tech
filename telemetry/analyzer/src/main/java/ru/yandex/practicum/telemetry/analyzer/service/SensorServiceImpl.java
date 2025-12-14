package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;

    @Override
    public void saveSensor(String deviceId, String hubId) {

        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(deviceId, hubId);

        if (sensor.isEmpty()) {
            Sensor newSensor = new Sensor();
            newSensor.setId(deviceId);
            newSensor.setHubId(hubId);

            sensorRepository.save(newSensor);
            log.info("Добавлен сенсор id = {}, hubId = {}", deviceId, hubId);
        } else {
            log.info("Сенсор id = {} был добавлен ранее hubId = {}", deviceId, hubId);
        }
    }

    @Override
    public void deleteSensor(String deviceId, String hubId) {

        Optional<Sensor> sensor = sensorRepository.findByIdAndHubId(deviceId, hubId);

        if (sensor.isPresent()) {
            sensorRepository.delete(sensor.get());
            log.info("Удален сенсор id = {}, hubId = {}", deviceId, hubId);
        } else {
            log.info("Не найден сенсор id = {} с hubId = {}", deviceId, hubId);
        }
    }
}
