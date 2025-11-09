package ru.yandex.practicum.telemetry.collector.controller;

public class BadArgumentsException extends RuntimeException {
    public BadArgumentsException(String message) {
        super(message);
    }
}
