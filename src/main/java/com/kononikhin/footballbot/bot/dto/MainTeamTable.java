package com.kononikhin.footballbot.bot.dto;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MainTeamTable {

    public static final List<String> FIRST_TABLE_ROW = List.of("Ц", "И", "В", "Н", "П", "МЗ", "МП", "РМ", "О");

    private final RosterType team;
    private final long totalGames;
    private final long wins;
    private final long draws;
    private final long looses;
    private final long goalsMade;
    private final long goalsGot;
    private final long difference;
    private final long points;

    public List<String> getRowData() {

        return List.of(team.getShortName(),
                String.valueOf(totalGames),
                String.valueOf(wins),
                String.valueOf(draws),
                String.valueOf(looses),
                String.valueOf(goalsMade),
                String.valueOf(goalsGot),
                String.valueOf(difference),
                String.valueOf(points));

    }

}
