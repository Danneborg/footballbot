package com.kononikhin.footballbot.bot.constants;

import lombok.Getter;

@Getter
//TODO этот энам должен заменить собою Step в PlayersSelector и GameDayData
public enum RosterType {

    RED,
    GREEN,
    BLUE,
    NAKED,
    ;


    public static RosterType getTypeFromStep(Step step) {

        switch (step) {
            case SELECT_RED_ROSTER -> {
                return RED;
            }
            case SELECT_BLUE_ROSTER -> {
                return BLUE;
            }
            case SELECT_GREEN_ROSTER -> {
                return GREEN;
            }
            case SELECT_NAKED_ROSTER -> {
                return NAKED;
            }
            default -> {
                throw new IllegalStateException("Не могу получить цвет команды из шага: " + step);
            }
        }

    }

}
