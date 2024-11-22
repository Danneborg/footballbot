package com.kononikhin.footballbot.bot;

import com.kononikhin.footballbot.bot.constants.Step;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//TODO для разных пользователей должен быть свой объект
//TODO покрыть тестами функционал методов
public class GameDayData {

    @Getter
    private final Set<String> selectedPlayers = new HashSet<>();
    private final Map<Step, Set<String>> rostersWithPlayers = new ConcurrentHashMap<>();
    private final Set<Step> gameDayRosters = new HashSet<>();
    private final Set<Step> fullRosters = new HashSet<>();

    public boolean isRosterSet(Step roster) {
        return rostersWithPlayers.containsKey(roster);
    }

    public boolean isRosterFull(Step roster) {

        //TODO бросить ошибку и обработать эту ситуацию с возвратом к предыдущему шагу
        if (!rostersWithPlayers.containsKey(roster)) {

        }

        return rostersWithPlayers.get(roster).size() == 5;

    }

    public Set<Step> getNotFullRosters() {
        Set<Step> difference = new HashSet<>(gameDayRosters); // Создаем копию
        difference.removeAll(fullRosters);
        return difference;
    }

    public void addRosterToFull(Step roster) {
        fullRosters.add(roster);
    }

    public void setRoster(Step roster) {
        rostersWithPlayers.put(roster, new HashSet<>());
        gameDayRosters.add(roster);
    }

    public void addPlayerToRoster(Step roster, String playerName) {
        rostersWithPlayers.get(roster).add(playerName);
        selectedPlayers.add(playerName);
    }

    public void addPlayerToSelectedPlayers(String player) {
        selectedPlayers.add(player);
    }

    public Set<String> getNotSelectedPlayers(Set<String> allPlayers) {
        Set<String> difference = new HashSet<>(allPlayers); // Создаем копию
        difference.removeAll(selectedPlayers);
        return difference;
    }

    public boolean areAllRostersFull(){
        return fullRosters.size() == gameDayRosters.size();
    }
}
