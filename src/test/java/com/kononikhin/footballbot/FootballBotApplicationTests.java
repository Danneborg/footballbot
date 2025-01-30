package com.kononikhin.footballbot;


import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.Goal;
import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.selectors.GameResultSelector;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;
import com.kononikhin.footballbot.bot.selectors.GameSessionStatisticSelector;
import com.kononikhin.footballbot.bot.selectors.PlayersSelector;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDateTime;
import java.util.*;

import static com.kononikhin.footballbot.bot.Utils.createMessage;


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

        var statisticSelector = new GameSessionStatisticSelector();

//        addPlayersToATeam(sessionData, RosterType.BLUE);
//        assert sessionData.isRosterFull(RosterType.BLUE);

        addPlayersToATeam(sessionData, RosterType.GREEN);
        assert sessionData.isRosterFull(RosterType.GREEN);

        addPlayersToATeam(sessionData, RosterType.RED);
        assert sessionData.isRosterFull(RosterType.RED);

        Map<Long, Step> userCurrentStep = new HashMap<>();
        Map<Long, SendMessage> userLastMessage = new HashMap<>();

        var startKeyBoard = Utils.createKeyBoard(Step.DEFAULT_BUTTON);
        var lastMessage = createMessage(chatId, startKeyBoard, Step.START);

        var redRosterPlayers = new ArrayList<>(sessionData.getRosterPlayers(RosterType.RED));
        var redRosterBombardier = redRosterPlayers.get(0);
        var redRosterAssistant = redRosterPlayers.get(1);

        gameResultSelector.setGameResult(chatId, Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep, lastMessage);
        //Назначаем бомбардира
        var message = gameResultSelector.setGameResult(chatId, String.format("%s:%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_BOMBARDIER.getConsoleCommand(), redRosterBombardier), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep, lastMessage);
        assert sessionData.getGameResults().size() == 1;
        assert !sessionData.getLastGameResult().isGameFinished();
        assert sessionData.getGameResults().get(0).isFirstTeamSet();
        assert !sessionData.getGameResults().get(0).isSecondTeamSet();
        assert sessionData.getGameResults().get(0).getNumberOfGoals(RosterType.RED) == 0;
        assert sessionData.getGameResults().get(0).getLastUncompletedGoalInfo(RosterType.RED).getBombardier().equals(redRosterBombardier);
        assert !sessionData.getGameResults().get(0).getLastUncompletedGoalInfo(RosterType.RED).isGoalComplete();

        //Назначаем ассистента
        var innerMessage = gameResultSelector.setGameResult(chatId, String.format("%s:%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_ASSISTANT.getConsoleCommand(), redRosterAssistant), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep, lastMessage);
        var lastCompletedGameOpt = sessionData.getGameResults().get(0).getLastCompletedGoalInfo(RosterType.RED);
        assert lastCompletedGameOpt.isPresent();
        assert lastCompletedGameOpt.get().getAssistant().equals(redRosterAssistant);

        var message11 = gameResultSelector.setGameResult(chatId, String.format("%s:%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_BOMBARDIER.getConsoleCommand(), redRosterBombardier), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep, lastMessage);
        var message12 = gameResultSelector.setGameResult(chatId, String.format("%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_NO_ASSISTANT.getConsoleCommand()), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep, lastMessage);

        assert sessionData.getLastGameResult().getNumberOfGoals(RosterType.RED) == 2;

        //Завершаем внесение результатов для первой команды
        var message1 = gameResultSelector.setGameResult(chatId, String.format("%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_NO_GOAL.getConsoleCommand()), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep, lastMessage);
        assert sessionData.getGameResults().get(0).getResult().get(RosterType.RED).isSingleGameScoreSet();
        var keyboard = (InlineKeyboardMarkup) message1.getReplyMarkup();

        assert keyboard.getKeyboard().get(0).size() == sessionData.getNumberOfFullRosters() - 1;
        for (var singleConsoleCommand : keyboard.getKeyboard().get(0)) {
            assert !singleConsoleCommand.getCallbackData().equals(Step.SET_RED_ROSTER_RESULT.getConsoleCommand());
        }

        assert message1.getText().equals("Выбери вторую сыгравшую команду");
        //TODO проверить отправку команды /set_red_roster_result:/set_no_assistant, добавить шаги внутри установки счета на проверку,
        // что нельзя указать кого либо из команды, результат которой в данной игре уже задан
        var message2 = gameResultSelector.setGameResult(chatId, String.format("%s:%s", Step.SET_RED_ROSTER_RESULT.getConsoleCommand(), Goal.SET_NO_ASSISTANT.getConsoleCommand()), sessionData, Step.SET_RED_ROSTER_RESULT, userCurrentStep, lastMessage);

        var greenRosterPlayers = new ArrayList<>(sessionData.getRosterPlayers(RosterType.GREEN));
        var greenRosterBombardier = redRosterPlayers.get(0);

        //Завершаем внесение результатов для второй команды
        var messageGreen = gameResultSelector.setGameResult(chatId, Step.SET_GREEN_ROSTER_RESULT.getConsoleCommand(), sessionData, Step.SET_GREEN_ROSTER_RESULT, userCurrentStep, lastMessage);
        assert messageGreen.getText().equals("Внеси результат команды Зеленые");
        var keyboardGreen = (InlineKeyboardMarkup) messageGreen.getReplyMarkup();
        assert keyboardGreen.getKeyboard().get(0).get(0).getCallbackData().equals("/set_green_roster_result:/set_bombardier");
        assert keyboardGreen.getKeyboard().get(0).get(0).getText().equals("Укажи бомбардира");
        assert keyboardGreen.getKeyboard().get(0).get(1).getCallbackData().equals("/set_green_roster_result:/set_no_goal");
        assert keyboardGreen.getKeyboard().get(0).get(1).getText().equals("Голы не забиты или голы закончились");

        var message3 = gameResultSelector.setGameResult(chatId, String.format("%s:%s", Step.SET_GREEN_ROSTER_RESULT.getConsoleCommand(), Goal.SET_NO_GOAL.getConsoleCommand()), sessionData, Step.SET_GREEN_ROSTER_RESULT, userCurrentStep, lastMessage);
        var keyboardFinish = (InlineKeyboardMarkup) message3.getReplyMarkup();

        assert sessionData.atLeastOneFinishedGame();
        assert keyboardFinish.getKeyboard().get(0).size() == 3;
        assert keyboardFinish.getKeyboard().get(0).get(keyboardFinish.getKeyboard().get(0).size() -1).getCallbackData().equals("/finish_a_game_day");

        var finalMessage = statisticSelector.createMessage(chatId, sessionData,  userCurrentStep);

        System.out.println();
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
