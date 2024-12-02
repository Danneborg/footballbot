package com.kononikhin.footballbot.bot.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO возможно все энамы можно слить в один, но тогда потеряется их смысловое разграничение, нужно подумать над этим
//TODO добавить возможность внести автогол
@Getter
@RequiredArgsConstructor
public enum Goal {

    //TODO добавить цвет команды к buttonText
    SET_BOMBARDIER("/set_bombardier", "Укажи бомбардира"),
    SET_ASSISTANT("/set_assistant", "Укажи ассистента"),
    NO_ASSISTANT("/set_no_assistant", "Ассистента нет"),
    SET_NO_GOAL("/set_no_goal", "Голы не забиты или голы закончились"),
    SET_SECOND_TEAM("/set_second_team", "Выбрать вторую команду"),
    UNKNOWN("/unknown", "Неизвестный шаг"),
    ;

    private final String consoleCommand;
    private final String buttonText;

    public static final List<Goal> POSSIBLE_GOAL_ACTORS = List.of(SET_BOMBARDIER, SET_ASSISTANT);
    public static final List<Goal> SET_BOMBARDIER_OR_NO_GOAL = List.of(SET_BOMBARDIER, SET_NO_GOAL);
    public static final List<Goal> SET_ASSIST_OR_NO_ASSIST = List.of(SET_ASSISTANT, NO_ASSISTANT);
    private static final Map<String, Goal> goalActors = new HashMap<>();

    static {
        for (Goal step : Goal.values()) {
            goalActors.put(step.consoleCommand, step);
        }
    }

    public static Goal fromConsoleCommand(String command) {
        //TODO возможно бросать ошибку и возвращать на предыдущий шаг
        return goalActors.getOrDefault(command, UNKNOWN);
    }


}
