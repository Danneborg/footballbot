package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
//TODO этот класс должен заменить собой класс Step в GameDayData
public class Roster {

    private final RosterType rosterType;
    private boolean isRosterFull = false;
    private final Set<String> selectedPlayers = new HashSet<>();

    public Roster(RosterType rosterType) {
        this.rosterType = rosterType;
    }

    public void addPlayer(String playerName) {
        selectedPlayers.add(playerName);

        if (selectedPlayers.size() == GameDayData.ROSTER_SIZE) {
            isRosterFull = true;
        }

    }
}
