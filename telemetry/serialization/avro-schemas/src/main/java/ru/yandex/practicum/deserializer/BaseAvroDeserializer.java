package ru.yandex.practicum.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final DecoderFactory decoderFactory;
    private final Schema schema;

    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.schema = schema;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) return null;

        try {
            BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
            DatumReader<T> datumReader = new SpecificDatumReader<>(schema);
            return datumReader.read(null, decoder);
        } catch (Exception e) {
            throw new SerializationException("Ошибка десериализации, топик " + topic, e);
        }
    }
}
