package com.kononikhin.footballbot.bot.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.kononikhin.footballbot.bot.constants.Step.*;

@Getter
@RequiredArgsConstructor
public enum RosterType {

    RED("Красные", "\uD83D\uDFE5", "К"),
    GREEN("Зеленые","\uD83D\uDFE9", "З"),
    BLUE("Синие","\uD83D\uDFE6", "С"),
    NAKED("Голые","⬜", "Г"),
    ;

    //TODO цвета можно вынести в отдельный энам
    private final String colour;
    private final String emojiCode;
    private final String shortName;

    @Getter
    private static final Set<RosterType> ALL_ROSTERS = new HashSet<>();

    public static Set<RosterType> getAllRosters(){
        return new HashSet<>(ALL_ROSTERS);
    }

    static {
        ALL_ROSTERS.addAll(Arrays.asList(RosterType.values()));
    }

    public static RosterType getRosterTypeFromStep(Step step) {

        switch (step) {
            case SELECT_RED_ROSTER, SET_RED_ROSTER_RESULT -> {
                return RED;
            }
            case SELECT_BLUE_ROSTER, SET_BLUE_ROSTER_RESULT -> {
                return BLUE;
            }
            case SELECT_GREEN_ROSTER, SET_GREEN_ROSTER_RESULT -> {
                return GREEN;
            }
            case SELECT_NAKED_ROSTER, SET_NAKED_ROSTER_RESULT -> {
                return NAKED;
            }
            default -> {
                throw new IllegalStateException("Не могу получить цвет команды из шага: " + step);
            }
        }

    }

    //TODO Выглядит так, что можно заменить все эти методы переместив шаги в поля самого энама
    public static Step getStepForSetGameResult(RosterType rosterType) {

        switch (rosterType) {
            case RED -> {
                return SET_RED_ROSTER_RESULT;
            }
            case BLUE -> {
                return SET_BLUE_ROSTER_RESULT;
            }
            case GREEN -> {
                return SET_GREEN_ROSTER_RESULT;
            }
            case NAKED -> {
                return SET_NAKED_ROSTER_RESULT;
            }
            default -> {
                throw new IllegalStateException("Не могу получить тип команды из типа ростера: " + rosterType);
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
