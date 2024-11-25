package com.kononikhin.footballbot;


import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.teamInfo.GameDayData;
import com.kononikhin.footballbot.bot.teamInfo.PlayersSelector;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class FootballbotApplicationTests {

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

    @Test
    public void testAddNewRoster() {

        Long chatId = 1L;
        var gameData = new GameDayData();
        var playersSelector = new PlayersSelector();
        Map<Long, Step> userCurrentStep = new HashMap<>();

        var incomingMessage = Step.SELECT_RED_ROSTER.getConsoleCommand();
        var roster = Step.SELECT_RED_ROSTER;

        var newMessage = playersSelector.createMessage(chatId, incomingMessage, gameData, roster, ALL_PLAYERS, userCurrentStep);

        assert newMessage != null;
        //TODO проверить содержимое newMessage

        var newMessageAfterAddingPlayer = playersSelector.createMessage(chatId, incomingMessage+":Player1", gameData, roster, ALL_PLAYERS, userCurrentStep);

        //Добавили 1 игрока в команду
        assert gameData.getRosterSize(roster) == 1;
        //В доступном списке на 1 игрока меньше
        assert gameData.getNotSelectedPlayers(ALL_PLAYERS).size() == ALL_PLAYERS.size() - 1;
        assert gameData.getRosterWithPlayers(roster).size() == 1;

    }

}
