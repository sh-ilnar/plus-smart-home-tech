package ru.yandex.practicum.telemetry.aggregator.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.service.AggregationService;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    // ... объявление полей и конструктора ...
    private final Consumer<Void, SensorEventAvro> consumer;
    private final Producer<Void, SpecificRecordBase> producer;
    private final AggregationService aggregationService;

    @Value("${plus-smart-home-tech.kafka.sensor-event-topic:telemetry.sensors.v1}")
    private String sensorEventTopic;

    @Value("${plus-smart-home-tech.kafka.sensor-snapshot-topic:telemetry.snapshots.v1}")
    private String sensorSnapshotTopic;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        try {
            log.info("Подготовка к агрегации данных");

            log.info("Подписка на топик: {}", sensorEventTopic);
            consumer.subscribe(Collections.singletonList(sensorEventTopic));

            while (true) {
                ConsumerRecords<Void, SensorEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<Void, SensorEventAvro> record : records) {

                    Optional<SensorsSnapshotAvro> snapshot = aggregationService.handleSensorEvent(record.value());

                    if (snapshot.isPresent()) {
                        ProducerRecord<Void, SpecificRecordBase> producerRecord =
                                new ProducerRecord<>(
                                        sensorSnapshotTopic,
                                        snapshot.get()
                                );
                        producer.send(producerRecord);
                    }
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitAsync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
