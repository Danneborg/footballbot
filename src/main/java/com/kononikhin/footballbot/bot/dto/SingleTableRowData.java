package com.kononikhin.footballbot.bot.dto;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SingleTableRowData {

    public static final List<String> FIRST_TABLE_ROW = List.of("Цвет", "Цвет", "Голы", "Голы");

    private final RosterType firstTeam;
    private final RosterType secondTeam;
    private final long firstTeamScore;
    private final long secondTeamScore;

    public List<String> getRowData() {

        return List.of(firstTeam.getColour(), secondTeam.getColour(), String.valueOf(firstTeamScore), String.valueOf(secondTeamScore));

    }

}
