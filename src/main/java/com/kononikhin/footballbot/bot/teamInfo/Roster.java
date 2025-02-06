package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfo;
import lombok.Getter;

import java.util.*;

@Getter
//TODO этот класс должен заменить собой класс Step в GameDayData
public class Roster {

    private final RosterType rosterType;
    private boolean isRosterFull = false;
    private final Set<String> selectedPlayers = new HashSet<>();
    //TODO этот список должен собой заменить поле playersByName
    private final Map<String, PlayerInfo> playersByName = new HashMap<>();
    //TODO использовать этот флаг для функционала игры в неполных составах
    private boolean playInNotFullRoster = false;

    public Roster(RosterType rosterType) {
        this.rosterType = rosterType;
    }

    public void addPlayer(String playerName, PlayerInfo playerInfo) {
        selectedPlayers.add(playerName);
        playersByName.put(playerName, playerInfo);
        if (selectedPlayers.size() == GameSessionData.ROSTER_SIZE) {
            isRosterFull = true;
        }

    }

    public List<PlayerInfo> getAllPlayers() {
        return new ArrayList<>(playersByName.values());
    }
}
