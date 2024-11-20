package com.kononikhin.footballbot.bot.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Steps {

    START("/start","Старт"),
    START_GAME_DAY("/start_game_day","Начать игровой день"),
    UNKNOWN("unknown","Неизвестный шаг"),
    ;

    private final String consoleCommand;
    private final String description;

    private static final Map<String, Steps> commandMap = new HashMap<>();

    static {
        for (Steps step : Steps.values()) {
            commandMap.put(step.consoleCommand, step);
        }
    }

    public static Steps fromConsoleCommand(String command) {
        return commandMap.getOrDefault(command, UNKNOWN);
    }

    //TODO вынести все доступные списки команд в отдельный энам или реализовать тут, чтобы не генерировать эти списки при каждом вызове
    public List<Steps> getNextStep(Steps previousStep) {

        switch (previousStep) {

            case START -> {
                return List.of(START_GAME_DAY);
            }
            case START_GAME_DAY -> {
                //TODO
                return List.of();
            }
            default -> {
                return List.of(UNKNOWN);
            }

        }

    }
}
