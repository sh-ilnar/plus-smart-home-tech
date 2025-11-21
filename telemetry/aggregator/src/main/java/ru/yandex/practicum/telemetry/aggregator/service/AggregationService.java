package ru.yandex.practicum.telemetry.aggregator.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

public interface AggregationService {

    Optional<SensorsSnapshotAvro> handleSensorEvent(SensorEventAvro event);

}
