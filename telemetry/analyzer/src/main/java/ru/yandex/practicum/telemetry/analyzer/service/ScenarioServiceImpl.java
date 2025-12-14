package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.*;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioServiceImpl implements ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;

    @Override
    public List<Scenario> getScenariosByHubId(String hubId) {
        return scenarioRepository.findByHubId(hubId);
    }

    @Override
    public void saveScenario(String hubId, ScenarioAddedEventAvro event) {
        String scenarioName = event.getName();
        Optional<Scenario> optionalScenario = scenarioRepository.findByHubIdAndName(hubId, event.getName());
        Scenario scenario;

        if (optionalScenario.isEmpty()) {
            Scenario newScenario = new Scenario();
            newScenario.setName(scenarioName);
            newScenario.setHubId(hubId);
            scenario = newScenario;
            log.info("Для хаба hubId = {} добавлен новый сценарий {}", hubId, scenarioName);
        } else {
            scenario = optionalScenario.get();

            Collection<Condition> conditions = scenario.getConditions().values();
            if (!conditions.isEmpty()) {
                conditionRepository.deleteAll(conditions);
                scenario.getConditions().clear();
            }

            Collection<Action> actions = scenario.getActions().values();
            if (!actions.isEmpty()) {
                actionRepository.deleteAll(actions);
                scenario.getActions().clear();
            }
        }

        List<ScenarioConditionAvro> conditions = event.getConditions();
        for (ScenarioConditionAvro condition : conditions) {
            Condition newCondition = new Condition();
            newCondition.setType(ConditionType.fromAvro(condition.getType()));
            newCondition.setOperation(ConditionOperation.fromAvro(condition.getOperation()));
            newCondition.setValue(toInteger(condition.getValue()));
            scenario.addCondition(condition.getSensorId(), newCondition);
        }

        List<DeviceActionAvro> actions = event.getAction();
        for (DeviceActionAvro action : actions) {
            Action newAction = new Action();
            newAction.setType(ActionType.fromAvro(action.getType()));
            scenario.addAction(action.getSensorId(), newAction);
        }

        conditionRepository.saveAll(scenario.getConditions().values());
        log.info("Условия для сценария {} сохранены", event.getName());

        actionRepository.saveAll(scenario.getActions().values());
        log.info("Действия для сценария {} сохранены", event.getName());

        scenarioRepository.save(scenario);
        log.info("Сценарий {} сохранен", event.getName());
    }

    @Override
    public void deleteScenario(String hubId, ScenarioRemovedEventAvro event) {
        String scenarioName = event.getName();
        Optional<Scenario> optionalScenario = scenarioRepository.findByHubIdAndName(hubId, event.getName());

        if (optionalScenario.isPresent()) {
            Scenario scenario = optionalScenario.get();
            conditionRepository.deleteAll(scenario.getConditions().values());
            actionRepository.deleteAll(scenario.getActions().values());
            scenarioRepository.delete(scenario);
            log.info("Сценарий {} удален", scenarioName);
        } else {
            log.info("Сценарий {} не существует", scenarioName);
        }
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof Boolean b) {
            return b ? 1 : 0;
        }
        log.warn("Неподдерживаемый тип: {}", value.getClass().getName());
        return null;
    }
}
