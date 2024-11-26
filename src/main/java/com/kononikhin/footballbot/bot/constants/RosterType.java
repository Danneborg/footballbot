package com.kononikhin.footballbot.bot.constants;

import lombok.Getter;

import java.util.*;

import static com.kononikhin.footballbot.bot.constants.Step.*;

@Getter
public enum RosterType {

    RED,
    GREEN,
    BLUE,
    NAKED,
    ;

    @Getter
    private static final Set<RosterType> ALL_ROSTERS = new HashSet<>();

    static {
        ALL_ROSTERS.addAll(Arrays.asList(RosterType.values()));
    }

    public static RosterType getRosterTypeFromStep(Step step) {

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

    public static Step getStepFromRosterType(RosterType rosterType) {

        switch (rosterType) {
            case RED -> {
                return SELECT_RED_ROSTER;
            }
            case BLUE -> {
                return SELECT_BLUE_ROSTER;
            }
            case GREEN -> {
                return SELECT_GREEN_ROSTER;
            }
            case NAKED -> {
                return SELECT_NAKED_ROSTER;
            }
            default -> {
                throw new IllegalStateException("Не могу получить тип команды из типа ростера: " + rosterType);
            }
        }

    }

}
