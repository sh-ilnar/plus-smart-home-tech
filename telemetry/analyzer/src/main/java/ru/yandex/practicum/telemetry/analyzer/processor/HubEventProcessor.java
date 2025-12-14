package ru.yandex.practicum.telemetry.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;

import java.time.Duration;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final Consumer<Void, HubEventAvro> consumer;
    private final HubEventHandler handler;

    public HubEventProcessor(Consumer<Void, HubEventAvro> consumer, HubEventHandler handler) {
        this.consumer = consumer;
        this.handler = handler;
    }

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    @Override
    public void run() {
        try {
            log.info("Старт HubEventProcessor");

            while(true) {
                ConsumerRecords<Void, HubEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                log.debug("Получаю {} записей.", records.count());

                records.forEach(record -> {
                    try {
                        handler.handleHubEvent(record.value());
                    } catch (Exception e) {
                        log.error("Ошибка при обработке записи из Topic: {}, Partition: {}, Offset: {}",
                                record.topic(), record.partition(), record.offset(), e);
                    }
                });

                try {
                    consumer.commitSync();
                    log.trace("Offset зафиксированы.");
                } catch (Exception e) {
                    log.error("Ошибка при фиксации Offset", e);
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки событий", e);
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
