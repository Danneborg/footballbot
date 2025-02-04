package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO покрыть тестами функционал методов
@RequiredArgsConstructor
public class GameSessionData {

    private final Long chatId;
    private final Long gameSessionDataDbId;
    private final UUID uuid;
    @Getter
    private final LocalDateTime sessionDateStart;
    private LocalDateTime sessionDateEnd;
    //TODO добавить проверку на то, что в указанном личном чате уже начата игровая сессия и ее нельзя начать заново, учитывая,
    // то что сейчас есть механика только для приватных чатов,
    // то нужно просто сохранять текущую активную сессию со всеми потрохами и доставать ее из бд, если они не загружена
    private boolean isFinished = false;
    private boolean playersLoaded = false;
    private Set<PlayerInfo> loadedPlayers = new HashSet<>();
    private Map<String, PlayerInfo> loadedPlayersToTgNames = new HashMap<>();

    @Getter
    private final List<GameResult> gameResults = new ArrayList<>();

    @Getter
    //TODO продумать механизм задания количества игроков в команде через действия пользователя
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

    //TODO протестировать механизм добавления новой свежей игры
    public GameResult getLastGameResult() {

        if (CollectionUtils.isEmpty(gameResults)) {
            gameResults.add(new GameResult());
        } else {
            if (gameResults.get(gameResults.size() - 1).isGameFinished()) {
                gameResults.add(new GameResult());
            }
        }

        return gameResults.get(gameResults.size() - 1);
    }

    public boolean atLeastOneFinishedGame() {
        return gameResults.stream()
                .map(GameResult::isGameFinished)
                .filter(Boolean::booleanValue)
                .findAny().orElse(false);
    }

    public Set<Step> getStepsForSetResult() {
        return rostersWithPlayers.keySet().stream()
                .map(RosterType::getStepForSetGameResult).collect(Collectors.toSet());
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

    public void addPlayerToRoster(RosterType rosterType, String playerName, PlayerInfo playerInfo) {
        rostersWithPlayers.get(rosterType).addPlayer(playerName, playerInfo);
    }

    public Set<String> getNotSelectedPlayers(Set<PlayerInfo> allPlayers) {
        var difference = allPlayers.stream()
                .map(PlayerInfo::getTgName)
                .collect(Collectors.toSet());
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

    public Set<String> getRosterPlayers(RosterType rosterType) {
        return new HashSet<>(rostersWithPlayers.get(rosterType).getSelectedPlayers());
    }

    public void setFinished() {
        isFinished = true;
    }

    public Long getGameSessionDataDbId() {
        return gameSessionDataDbId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setListOfPlayers(Set<PlayerInfo> listOfPlayers) {
        this.loadedPlayers = new HashSet<>(listOfPlayers);
        loadedPlayersToTgNames = loadedPlayers.stream().collect(Collectors.toMap(PlayerInfo::getTgName, e -> e));
    }

    public boolean isPlayersLoaded() {
        return playersLoaded;
    }

    public void setPlayersLoaded(boolean playersLoaded) {
        this.playersLoaded = playersLoaded;
    }

    public Set<PlayerInfo> getLoadedPlayers() {
        return loadedPlayers;
    }

    public void setLoadedPlayers(Set<PlayerInfo> loadedPlayers) {
        this.loadedPlayers = loadedPlayers;
        loadedPlayersToTgNames = loadedPlayers.stream().collect(Collectors.toMap(PlayerInfo::getTgName, e -> e));
    }

    public PlayerInfo getPlayerInfoByTgName(String playerName) {
        return loadedPlayersToTgNames.get(playerName);
    }
}
