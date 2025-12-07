package ru.yandex.practicum.telemetry.analyzer.handler;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.entity.Action;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioService;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SnapshotHandler {

    private final ScenarioService scenarioService;
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public SnapshotHandler(@GrpcClient("hub-router")
                           HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient,
                           ScenarioService scenarioService) {
        this.hubRouterClient = hubRouterClient;
        this.scenarioService = scenarioService;
    }

    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();

        Map<String, SensorStateAvro> sensorStateAvroMap = snapshot.getSensorsState();

        List<Scenario> scenarios = scenarioService.getScenariosByHubId(hubId);

        scenarios
                .stream()
                .filter(scenario -> ConditionAnalyzer.isSatisfiedConditions(snapshot, scenario.getConditions()))
                .forEach(this::activateAction);
    }

    private void activateAction(Scenario scenario) {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(Instant.now().getEpochSecond())
                .setNanos(Instant.now().getNano())
                .build();

        for (Map.Entry<String, Action> entry : scenario.getActions().entrySet()) {
            String sensorId = entry.getKey();
            Action action = entry.getValue();

            try {
                ActionTypeProto protoType = ActionTypeProto.valueOf(action.getType().name());

                DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                        .setSensorId(sensorId)
                        .setType(protoType);

                if (action.getValue() != null) {
                    actionBuilder.setValue(action.getValue());
                }

                hubRouterClient.handleDeviceAction(DeviceActionRequest.newBuilder()
                        .setHubId(scenario.getHubId())
                        .setScenarioName(scenario.getName())
                        .setAction(actionBuilder.build())
                        .setTimestamp(timestamp)
                        .build());

                log.info("Отправлено действие на роутер: сенсор {}, тип {}, значение {}",
                        sensorId, protoType, action.getValue());

            } catch (Exception e) {
                log.error("Ошибка при отправке действия для сценария {}: {}", scenario.getName(), e.getMessage(), e);
            }
        }
    }
}
