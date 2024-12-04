package com.kononikhin.footballbot;


import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.Goal;
import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.teamInfo.GameResultSelector;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;
import com.kononikhin.footballbot.bot.teamInfo.PlayersSelector;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;


@SpringBootTest
public class FootballBotApplicationTests {

    private final static Set<String> ALL_PLAYERS = Set.of("Player1", "Player2", "Player3", "Player4", "Player5", "Player6", "Player7", "Player8", "Player9", "Player10", "Player11", "Player12", "Player13", "Player14", "Player15", "Player16", "Player17", "Player18", "Player19", "Player20", "Player21", "Player22", "Player23");

    @Test
    public void contextLoads() {

        var numberOfRows = Utils.defineNumberOfRows(1);
        assert numberOfRows == 1;
        var numberOfRows1 = Utils.defineNumberOfRows(4);
        assert numberOfRows1 == 1;
        var numberOfRows2 = Utils.defineNumberOfRows(5);
        assert numberOfRows2 == 2;
        var numberOfRows3 = Utils.defineNumberOfRows(8);
        assert numberOfRows3 == 2;
        var numberOfRows4 = Utils.defineNumberOfRows(13);
        assert numberOfRows4 == 4;
    }

    private void addPlayersToATeam(GameSessionData gameSessionData, RosterType rosterType) {
        gameSessionData.setRoster(rosterType);

        var notSelectedPlayers = new ArrayList<>(gameSessionData.getNotSelectedPlayers(ALL_PLAYERS));

        for (int i = 0; i < GameSessionData.ROSTER_SIZE; i++) {
            gameSessionData.addPlayerToRoster(rosterType, notSelectedPlayers.get(i));
        }

    }

    @Test
    public void testProcessGameSessionData() {

        var gameResultSelector = new GameResultSelector();

        var chatId = 1L;

        var sessionData = new GameSessionData(chatId, UUID.randomUUID(), LocalDateTime.now());

        addPlayersToATeam(sessionData, RosterType.BLUE);
        assert sessionData.isRosterFull(RosterType.BLUE);

        addPlayersToATeam(sessionData, RosterType.GREEN);
        assert sessionData.isRosterFull(RosterType.GREEN);

        addPlayersToATeam(sessionData, RosterType.RED);
        assert sessionData.isRosterFull(RosterType.RED);

        Map<Long, Step> userCurrentStep = new HashMap<>();

        var redRosterPlayers = new ArrayList<>(sessionData.getRosterPlayers(RosterType.RED));
        var redRosterPlayer1 = redRosterPlayers.get(0);
        var redRosterPlayer2 = redRosterPlayers.get(1);

        gameResultSelector.setGameResult(chatId, Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep);
        var message = gameResultSelector.setGameResult(chatId, String.format("%s:%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_BOMBARDIER.getConsoleCommand(), redRosterPlayer1), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep);
        assert sessionData.getGameResults().size() == 1;
        assert !sessionData.getLastGameResult().isGameFinished();
        assert sessionData.getGameResults().get(0).isFirstTeamSet();
        assert !sessionData.getGameResults().get(0).isSecondTeamSet();
        assert sessionData.getGameResults().get(0).getNumberOfGoals(RosterType.RED) == 0;
        assert sessionData.getGameResults().get(0).getLastUncompletedGoalInfo(RosterType.RED).getBombardier().equals(redRosterPlayer1);
        assert !sessionData.getGameResults().get(0).getLastUncompletedGoalInfo(RosterType.RED).isGoalComplete();

        gameResultSelector.setGameResult(chatId, String.format("%s:%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_ASSISTANT.getConsoleCommand(), redRosterPlayer2), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep);
        var lastCompletedGameOpt = sessionData.getGameResults().get(0).getLastCompletedGoalInfo(RosterType.RED);
        assert lastCompletedGameOpt.isPresent();
        assert lastCompletedGameOpt.get().getAssistant().equals(redRosterPlayer2);

        gameResultSelector.setGameResult(chatId, String.format("%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_NO_GOAL.getConsoleCommand()), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep);
        assert sessionData.getGameResults().get(0).getResult().get(RosterType.RED).isSingleGameScoreSet();
        //TODO проверить отправку команды /set_red_roster_result:/set_no_assistant

    }


    @Test
    public void testAddNewRoster() {

        Long chatId = 1L;
        var gameData = new GameSessionData(chatId, UUID.randomUUID(), LocalDateTime.now());
        var playersSelector = new PlayersSelector();
        Map<Long, Step> userCurrentStep = new HashMap<>();

        var incomingMessage = Step.SELECT_RED_ROSTER.getConsoleCommand();
        var roster = Step.SELECT_RED_ROSTER;
        var rosterType = RosterType.getRosterTypeFromStep(roster);

        var newMessage = playersSelector.createMessage(chatId, incomingMessage, gameData, roster, ALL_PLAYERS, userCurrentStep);

        assert newMessage != null;
        //TODO проверить содержимое newMessage

        var newMessageAfterAddingPlayer = playersSelector.createMessage(chatId, incomingMessage + ":Player1", gameData, roster, ALL_PLAYERS, userCurrentStep);

        //Добавили 1 игрока в команду
        assert gameData.getRosterSize(rosterType) == 1;
        //В доступном списке на 1 игрока меньше
        assert gameData.getNotSelectedPlayers(ALL_PLAYERS).size() == ALL_PLAYERS.size() - 1;
        assert gameData.getRosterPlayers(rosterType).size() == 1;

    }

}
