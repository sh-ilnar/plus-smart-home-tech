package ru.yandex.practicum.telemetry.collector.configuration;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Value("${plus-smart-home-tech.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private KafkaProducer<Void, SpecificRecordBase> kafkaProducer;

    @Bean
    public KafkaProducer<Void, SpecificRecordBase> kafkaProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.telemetry.collector.configuration.GeneralAvroSerializer");
        return new KafkaProducer<>(properties);
    }

    @PreDestroy
    public void closeProducer() {
        if (kafkaProducer != null) {
            kafkaProducer.flush();
            kafkaProducer.close(Duration.ofSeconds(10));
        }
    }
}
