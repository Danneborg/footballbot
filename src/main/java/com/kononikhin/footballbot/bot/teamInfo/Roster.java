package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
//TODO этот класс должен заменить собой класс Step в GameDayData
public class Roster {

    private final RosterType rosterType;
    private boolean isRosterFull = false;
    private final Set<String> selectedPlayers = new HashSet<>();
    //TODO этот список должен собой заменить поле selectedPlayers
    private final List<PlayerInfo> selectedPlayersDto = new ArrayList<>();
    //TODO использовать этот флаг для функционала игры в неполных составах
    private boolean playInNotFullRoster = false;

    public Roster(RosterType rosterType) {
        this.rosterType = rosterType;
    }

    public void addPlayer(String playerName) {
        selectedPlayers.add(playerName);

        if (selectedPlayers.size() == GameSessionData.ROSTER_SIZE) {
            isRosterFull = true;
        }

    }
}
