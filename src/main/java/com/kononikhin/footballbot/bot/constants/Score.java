package com.kononikhin.footballbot.bot.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Score {


    SCORE_ZERO("/score_zero", 0),
    SCORE_ONE("/score_one", 1),
    SCORE_TWO("/score_two", 2),
    SCORE_THREE("/score_three", 3),
    SCORE_FOUR("/score_four", 4),
    ;

    private final String consoleCommand;
    private final int buttonText;

    public static final List<Score> POSSIBLE_SCORE_LIST = List.of(SCORE_ZERO, SCORE_ONE, SCORE_TWO, SCORE_THREE, SCORE_FOUR);
    private static final Map<String, Score> scoreCommandMap = new HashMap<>();

    static {
        for (Score step : Score.values()) {
            scoreCommandMap.put(step.consoleCommand, step);
        }
    }

    public static Score fromConsoleCommand(String command) {
        //TODO возможно бросать ошибку и возвращать на предыдущий шаг
        return scoreCommandMap.getOrDefault(command, SCORE_ZERO);
    }

}
