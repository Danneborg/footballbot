package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.dao.pojo.*;
import com.kononikhin.footballbot.bot.dao.repo.*;
import com.kononikhin.footballbot.bot.teamInfo.GameResult;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;
import com.kononikhin.footballbot.bot.teamInfo.SingleGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class GameSessionServiceImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final RosterRepository rosterRepository;
    private final PlayerInfoToRosterRepository playerInfoToRosterRepository;
    private final SingleGameResultRepository singleGameResultRepository;
    private final SingleGoalInfoRepository singleGoalInfoRepository;

    @Override
    public GameSessionData createNewGameSessionData(Long chatId) {
        var newSession = GameSession.createNewSession(chatId);

        gameSessionRepository.save(newSession);

        return new GameSessionData(chatId, newSession.getId(), UUID.randomUUID(), newSession.getStartDate());
    }

    @Override
    public GameSession getUnfinishedGamesSessionByChatId(Long chatId) {
        return gameSessionRepository.findByChatIdAndIsFinished(chatId, false);
    }

    @Override
    public GameSessionData getUnfinishedGameSessionDataAndCreateIfDoesntExistByChatId(Long chatId) {

        var gameSession = gameSessionRepository.findByChatIdAndIsFinished(chatId, false);

        //Создаем новую игровую сессии
        if (gameSession == null) {

            var newSession = GameSession.createNewSession(chatId);

            gameSessionRepository.save(newSession);

            return new GameSessionData(chatId, newSession.getId(), UUID.randomUUID(), newSession.getStartDate());
        }


        //TODO этот шаг должен восстановить всё состояние игровой сессии из базы данных при сценарии,
        // когда игровая сессия началась, но сервер с ботом упал и потерял все inmemory данные
        return restoreGameSessionData(chatId);
    }

    @Override
    @Transactional
    public void finishGameSession(Long gameSessionDataDbId) {
        gameSessionRepository.setIsFinished(gameSessionDataDbId, true);
    }

    private GameSessionData restoreGameSessionData(Long chatId) {
        return null;
    }

    //TODO необходим механизм маппинга дто в энтити
    @Override
    @Transactional
    public void saveGameSessionData(Long chatId, GameSessionData tempGameData) {

        var currentGameSessionOpt = gameSessionRepository.findById(tempGameData.getGameSessionDataDbId());

        //TODO что делать если нет сессии?
        if (currentGameSessionOpt.isEmpty()) {

        }

        var currentGameSession = currentGameSessionOpt.get();

        var rostersWithPlayers = tempGameData.getRostersWithPlayers();

        Map<RosterType, Roster> dbRostersById = new HashMap<>();

        for (var singleRoster : rostersWithPlayers.values()) {

            var dbRoster = new Roster();
            dbRoster.setGameSession(currentGameSession);
            dbRoster.setTeamColour(singleRoster.getRosterType().getColour());
            dbRoster.setPlayedInNotFullRoster(singleRoster.isPlayInNotFullRoster());

            var newRoster = rosterRepository.save(dbRoster);
            dbRostersById.put(singleRoster.getRosterType(), newRoster);


            var playerInfoToRosterList = createPlayerInfoToRosterEntity(newRoster, singleRoster.getAllPlayers());

            playerInfoToRosterRepository.saveAll(playerInfoToRosterList);

        }

        for (var singleGameResult : tempGameData.getGameResults()) {

            var singleGameResultInDb = new SingleGameResult();
            singleGameResultInDb.setGameSession(currentGameSession);
            singleGameResultInDb.setIsDraw(singleGameResult.isDraw());

            singleGameResultInDb.setWinnerRoster(dbRostersById.get(singleGameResult.getWinner()));
            singleGameResultInDb.setLooserRoster(dbRostersById.get(singleGameResult.getLooser()));

            singleGameResultInDb.setWinnerScore(singleGameResult.getWinnerInfo().getNumberOfGoals());
            singleGameResultInDb.setLooserScore(singleGameResult.getLooserInfo().getNumberOfGoals());

            var tempSingleGameResultInDb = singleGameResultRepository.save(singleGameResultInDb);

            var allGoals = createAllGoals(singleGameResult, currentGameSession, tempSingleGameResultInDb, dbRostersById, rostersWithPlayers);

            singleGoalInfoRepository.saveAll(allGoals);
        }

        finishGameSession(tempGameData.getGameSessionDataDbId());
    }

    private List<PlayerInfoToRoster> createPlayerInfoToRosterEntity(Roster roster, List<PlayerInfo> players) {

        List<PlayerInfoToRoster> playerInfoToRosterList = new ArrayList<>();

        for (var singlePlayer : players) {
            var tempPlayerInfoToRoster = new PlayerInfoToRoster();
            tempPlayerInfoToRoster.setRoster(roster);
            tempPlayerInfoToRoster.setPlayer(singlePlayer);
            playerInfoToRosterList.add(tempPlayerInfoToRoster);
        }

        return playerInfoToRosterList;
    }

    private List<SingleGoalInfo> createAllGoals(GameResult gameResult, GameSession gameSession, SingleGameResult singleGameResult, Map<RosterType, Roster> rosterByType, Map<RosterType, com.kononikhin.footballbot.bot.teamInfo.Roster> rosterWithPlayers) {

        List<SingleGoalInfo> allGoals = new ArrayList<>();

        for (var singleResult : gameResult.getResult().entrySet()) {

            if (singleResult.getValue().getNumberOfGoals() > 0) {
                var tempGoals = createGoals(gameSession, singleGameResult, rosterByType.get(singleResult.getKey()), rosterWithPlayers.get(singleResult.getKey()).getPlayersByName(), singleResult.getValue().getGoals());
                allGoals.addAll(tempGoals);
            }
        }

        return allGoals;
    }

    private List<SingleGoalInfo> createGoals(
            GameSession gameSession,
            SingleGameResult singleGameResult,
            Roster roster,
            Map<String, PlayerInfo> rosterPlayers,
            List<SingleGoal> goals) {

        List<SingleGoalInfo> goalsList = new ArrayList<>();

        for (var singleGoal : goals) {
            var tempSingleGoal = new SingleGoalInfo();
            tempSingleGoal.setRoster(roster);
            tempSingleGoal.setGameSession(gameSession);
            tempSingleGoal.setSingleGameResult(singleGameResult);

            tempSingleGoal.setBombardier(rosterPlayers.get(singleGoal.getBombardier()));
            if (singleGoal.getAssistant() != null) {
                tempSingleGoal.setAssistant(rosterPlayers.get(singleGoal.getAssistant()));
            }

            goalsList.add(tempSingleGoal);
        }

        return goalsList;
    }
}
