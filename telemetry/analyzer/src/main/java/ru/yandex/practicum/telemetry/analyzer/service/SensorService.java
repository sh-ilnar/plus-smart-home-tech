package ru.yandex.practicum.telemetry.analyzer.service;

public interface SensorService {
    void saveSensor(String deviceId, String hubId);
    void deleteSensor(String deviceId, String hubId);
}
