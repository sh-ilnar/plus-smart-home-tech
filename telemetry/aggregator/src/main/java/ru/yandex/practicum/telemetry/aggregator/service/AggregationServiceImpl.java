package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService {

    private final HashMap<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> handleSensorEvent(SensorEventAvro event) {

        if (event == null) return Optional.empty();

        String hubId = event.getHubId();
        SensorsSnapshotAvro snapshot;

        if (!snapshots.containsKey(hubId)) {
            snapshot = createSnapshot(event);
            snapshots.put(hubId, snapshot);
            return Optional.of(snapshot);
        }

        snapshot = snapshots.get(hubId);
        Map<String, SensorStateAvro> states = snapshot.getSensorsState();
        String eventId = event.getId();

        if (states.containsKey(eventId)) {
            SensorStateAvro oldState = states.get(eventId);

            if (Objects.equals(oldState.getData(), event.getPayload())
                    || (oldState.getTimestamp().isAfter(event.getTimestamp()))) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        states.put(eventId, newState);
        snapshots.put(hubId, snapshot);

        return Optional.of(snapshot);
    }

    private SensorsSnapshotAvro createSnapshot(SensorEventAvro event) {

        Map<String, SensorStateAvro> states = new HashMap<>();

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        states.put(event.getId(), newState);

        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setSensorsState(states)
                .build();
    }
}


