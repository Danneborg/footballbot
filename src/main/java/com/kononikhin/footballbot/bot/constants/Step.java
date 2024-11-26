package com.kononikhin.footballbot.bot.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Step {

    //TODO Возможно есть смысл вынести описания шагов в отдельный энам, чтобы в нем отдельно редачить описания этапов с разметкой и тд
    START("/start", "Старт", "Стартовый этап, выбери доступное действие :"),
    START_GAME_DAY("/start_game_day", "Начать игровой день", "Игровой день начат, нужно выбрать составы команд : "),
    GET_COMMON_STAT("/get_common_stat", "Показать статистику", "Выбери тип статистики, который нужно отобразить : "),
    //TODO добавить длинное описалово того, что может делать бот
    SHOW_HELP("/show_help", "Помощь", ""),
    UNKNOWN("unknown", "Неизвестный шаг", "Шаг неизвестен, отправляю тебя на предыдущее действие..."),
    //Очень хитрый шаг, нужно передавать команду на выбор игроков из списка и одновременно в callBackData передать имя выбранного игрока
    SELECT_RED_ROSTER("/select_red_roster", "Красные", "Выбери игроков для Красной команды"),
    SELECT_GREEN_ROSTER("/select_green_roster", "Зеленые", "Выбери игроков для Зеленой команды"),
    SELECT_BLUE_ROSTER("/select_blue_roster", "Синие", "Выбери игроков для Синей команды"),
    SELECT_NAKED_ROSTER("/select_naked_roster", "Голые", "Выбери игроков для Раздетой команды"),
    TO_RESULT_SETTING("/to_result_setting", "Перейти к внесению результатов игр", "Отлично! Все составы набраны, можно вносить результаты игр!"),
    SET_A_SINGLE_RESULT("/set_a_single_result", "Внести результат", "Внеси результат игры или закончи игровой день."),
    FINISH_A_GAME_DAY("/finish_a_game_day", "Завершить игровой день.", "Отлично поиграли сегодня! Завершить игровой день и отправить статистику в чат."),
    ;

    private final String consoleCommand;
    private final String buttonText;
    private final String stepDescription;


    public static final List<Step> DEFAULT_BUTTON = List.of(START);
    private static final List<Step> BUTTON_START_LIST = List.of(START_GAME_DAY, GET_COMMON_STAT, SHOW_HELP);
    private static final List<Step> ROSTERS = List.of(SELECT_RED_ROSTER, SELECT_GREEN_ROSTER, SELECT_BLUE_ROSTER, SELECT_NAKED_ROSTER);
    private static final Map<String, Step> commandMap = new HashMap<>();
    public static final List<Step> PLAYER_SELECTION_TRIGGERS = List.of(SELECT_RED_ROSTER, SELECT_GREEN_ROSTER, SELECT_BLUE_ROSTER, SELECT_NAKED_ROSTER);

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
                return ROSTERS;
            }
            case SELECT_RED_ROSTER, SELECT_GREEN_ROSTER, SELECT_BLUE_ROSTER, SELECT_NAKED_ROSTER -> {
                return List.of(step);
            }

            case TO_RESULT_SETTING -> {
                return List.of(SET_A_SINGLE_RESULT);
            }

            default -> {
                return List.of(UNKNOWN);
            }

        }

    }

    public static Step fromConsoleCommand(String command) {

        if (command.startsWith(SELECT_RED_ROSTER.getConsoleCommand())
                || command.startsWith(SELECT_GREEN_ROSTER.getConsoleCommand())
                || command.startsWith(SELECT_BLUE_ROSTER.getConsoleCommand())
                || command.startsWith(SELECT_NAKED_ROSTER.getConsoleCommand())) {
            command = command.split(":")[0];
        }

        return commandMap.getOrDefault(command, UNKNOWN);
    }

    public static List<Step> listOfRosters(){
        return ROSTERS;
    }
}
