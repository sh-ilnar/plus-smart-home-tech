package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.handler.SnapshotHandler;

import java.time.Duration;

@Slf4j
@Component
public class SnapshotProcessor {

    private final Consumer<Void, SensorsSnapshotAvro> consumer;
    private final SnapshotHandler service;

    public SnapshotProcessor(Consumer<Void, SensorsSnapshotAvro> consumer, SnapshotHandler service) {
        this.consumer = consumer;
        this.service = service;
    }

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public void start() {
        try {
            log.info("Старт SnapshotProcessor");

            while (true) {
                ConsumerRecords<Void, SensorsSnapshotAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<Void, SensorsSnapshotAvro> record : records) {
                    service.handleSnapshot(record.value());
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        } finally {
            try {
                consumer.commitAsync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}
