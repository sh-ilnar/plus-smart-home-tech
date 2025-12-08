package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.configuration.EventTopics;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;

import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService{

    private final KafkaProducer<Void, SpecificRecordBase> kafkaProducer;

    private final EventTopics eventTopics;

    @Override
    public void handleSensorEvent(SensorEventProto sensorEvent) {

        log.info("Получено: {}", sensorEvent);

        SpecificRecordBase avroSensorEvent = SensorEventMapper.mapToAvro(sensorEvent);
        String topic = eventTopics.getSensorEventsTopic();

        ProducerRecord<Void, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic,
                        avroSensorEvent
                );
        kafkaProducer.send(record);

        log.info("Отправлено: {}", avroSensorEvent);
    }

    @Override
    public void handleHubEvent(HubEventProto hubEvent) {
        log.info("Получено: {}", hubEvent);

        SpecificRecordBase avroHubEvent = HubEventMapper.mapToAvro(hubEvent);
        String topic = eventTopics.getHubEventsTopic();

        ProducerRecord<Void, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic,
                        avroHubEvent
                );



        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Сообщение отправлено. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset());
            } else {
                log.error("Ошибка отправки в Kafka", exception);
            }
        });

        log.info("Отправлено: {}", avroHubEvent);
    }
}
