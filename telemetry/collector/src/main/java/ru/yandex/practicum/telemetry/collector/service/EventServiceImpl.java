package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.collector.configuration.EventTopics;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService{

    private final KafkaProducer<Void, SpecificRecordBase> kafkaProducer;

    private final EventTopics eventTopics;

    @Override
    public void handleSensorEvent(SensorEvent sensorEvent) {

        log.info("Получено: {}", sensorEvent);

        SpecificRecordBase avroSensorEvent = SensorEventMapper.mapToAvro(sensorEvent);
        String topic = eventTopics.getSensorEventsTopic();

        ProducerRecord<Void, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic,
                        null,
                        sensorEvent.getTimestamp().toEpochMilli(),
                        null,
                        avroSensorEvent
                );
        kafkaProducer.send(record);

        log.info("Отправлено: {}", avroSensorEvent);
    }

    @Override
    public void handleHubEvent(HubEvent hubEvent) {
        log.info("Получено: {}", hubEvent);

        SpecificRecordBase avroHubEvent = HubEventMapper.mapToAvro(hubEvent);
        String topic = eventTopics.getHubEventsTopic();

        ProducerRecord<Void, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic,
                        null,
                        hubEvent.getTimestamp().toEpochMilli(),
                        null,
                        avroHubEvent
                );
        kafkaProducer.send(record);

        log.info("Отправлено: {}", avroHubEvent);
    }
}
