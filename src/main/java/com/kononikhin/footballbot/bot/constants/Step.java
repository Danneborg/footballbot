package com.kononikhin.footballbot.bot.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Step {

    START("/start","Старт"),
    START_GAME_DAY("/start_game_day","Начать игровой день"),
    GET_COMMON_STAT("/get_common_stat","Показать статистику"),
    SHOW_HELP("/show_help","Помощь"),
    UNKNOWN("unknown","Неизвестный шаг"),
    ;

    private final String consoleCommand;
    private final String description;


    public static final List<Step> DEFAULT_BUTTON = List.of(START);
    private static final List<Step> BUTTON_START_LIST = List.of(START_GAME_DAY, GET_COMMON_STAT, SHOW_HELP);
    private static final Map<String, Step> commandMap = new HashMap<>();

    static {
        for (Step step : Step.values()) {
            commandMap.put(step.consoleCommand, step);
        }
    }

    //TODO вынести все доступные списки команд в отдельный энам или реализовать тут, чтобы не генерировать эти списки при каждом вызове
    public static List<Step> getNextStep(String previousStep) {

        var step = fromConsoleCommand(previousStep);

        switch (step) {

            case START -> {
                return BUTTON_START_LIST;
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

    public static Step fromConsoleCommand(String command) {
        return commandMap.getOrDefault(command, UNKNOWN);
    }
}
