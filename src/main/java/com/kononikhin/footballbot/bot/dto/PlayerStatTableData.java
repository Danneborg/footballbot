package com.kononikhin.footballbot.bot.dto;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PlayerStatTableData {

    public static final List<String> FIRST_TABLE_ROW = List.of("Цвет", "Имя", "Голы", "Асиссты");

    private final RosterType firstTeam;
    private final String name;
    @Getter
    private long goals;
    private long assist;

    public List<String> getRowData() {

        return List.of(firstTeam.getColour(), name, String.valueOf(goals), String.valueOf(assist));

    }

    public void addGoal(){
        goals++;
    }

    public void addAssist(){
        assist++;
    }
}
