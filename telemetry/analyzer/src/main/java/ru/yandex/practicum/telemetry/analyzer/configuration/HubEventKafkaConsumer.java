package ru.yandex.practicum.telemetry.analyzer.configuration;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.deserializer.HubEventDeserializer;
import ru.yandex.practicum.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Configuration
public class HubEventKafkaConsumer {

    @Value("${analyzer.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${analyzer.kafka.hub-event-topic}")
    private String hubEventTopic;

    @Bean
    public Consumer<Void, HubEventAvro> hubEventConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        Consumer<Void, HubEventAvro> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of(hubEventTopic));
        return consumer;
    }

    @PreDestroy
    public void closeAllConsumers() {

    }
}
