package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.constants.Step;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO для разных пользователей должен быть свой объект
//TODO покрыть тестами функционал методов
public class GameDayData {

    @Getter
    public static int ROSTER_SIZE = 5;

    @Getter
    private final Map<RosterType, Roster> rostersWithPlayers = new ConcurrentHashMap<>();

    public boolean isRosterSet(RosterType roster) {
        return rostersWithPlayers.containsKey(roster);
    }

    public boolean isRosterFull(RosterType rosterType) {

        //TODO бросить ошибку и обработать эту ситуацию с возвратом к предыдущему шагу
        if (!rostersWithPlayers.containsKey(rosterType)) {

        }

        return rostersWithPlayers.get(rosterType).isRosterFull();

    }

    //TODO должны быть виды типы шагов
    //TODO переименовать
    public List<Step> getAllRostersTypes() {

        var fullRosters = rostersWithPlayers.entrySet().stream()
                .filter(e -> e.getValue().isRosterFull())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        var tempAllRosters = RosterType.getALL_ROSTERS();
        tempAllRosters.removeAll(fullRosters);

        return tempAllRosters.stream()
                .map(RosterType::getStepFromRosterType)
                .collect(Collectors.toList());
    }

    public List<Step> getNotFullRosters() {

        var fullRosters = rostersWithPlayers.entrySet().stream()
                .filter(e -> e.getValue().isRosterFull())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        var tempAllRosters = RosterType.getALL_ROSTERS();
        tempAllRosters.removeAll(fullRosters);

        return tempAllRosters.stream()
                .map(RosterType::getStepFromRosterType)
                .collect(Collectors.toList());
    }

    public void setRoster(RosterType rosterType) {
        rostersWithPlayers.computeIfAbsent(rosterType, e -> new Roster(rosterType));
    }

    public void addPlayerToRoster(RosterType rosterType, String playerName) {
        rostersWithPlayers.get(rosterType).addPlayer(playerName);
    }

    public Set<String> getNotSelectedPlayers(Set<String> allPlayers) {
        var difference = new HashSet<>(allPlayers);
        var selectedPlayers = rostersWithPlayers.values().stream()
                .flatMap(e -> e.getSelectedPlayers().stream())
                .collect(Collectors.toSet());

        difference.removeAll(selectedPlayers);
        return difference;
    }

    public long getNumberOfFullRosters() {
        return rostersWithPlayers.values().stream()
                .filter(Roster::isRosterFull)
                .count();
    }

    public boolean isGameDayReadyToStart(int minimumFullRostersToPlay) {

        var fullRosters = getNumberOfFullRosters();

        return fullRosters >= minimumFullRostersToPlay && areAllRostersFull();

    }

    private boolean areAllRostersFull() {

        return rostersWithPlayers.values().stream()
                .allMatch(Roster::isRosterFull);

    }

    public int getRosterSize(RosterType rosterType) {
        return rostersWithPlayers.get(rosterType).getSelectedPlayers().size();
    }

    public Set<String> getRosterWithPlayers(RosterType rosterType) {
        return rostersWithPlayers.get(rosterType).getSelectedPlayers();
    }
}
