package com.kononikhin.footballbot.bot.teamInfo;

import com.github.aneureka.exception.FieldsNotSetException;
import com.github.aneureka.exception.RowSizeMismatchException;
import com.github.aneureka.util.PrettyTable;
import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.dto.MainTeamTable;
import com.kononikhin.footballbot.bot.dto.PlayerStatTableData;
import com.kononikhin.footballbot.bot.dto.RosterTypeGameStatistic;
import com.kononikhin.footballbot.bot.dto.SingleTableRowData;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;
import java.util.function.Consumer;

@Component
public class GameSessionStatisticSelector {

    //TODO надо как-то написать на это тест, хз вообще как, но сделать
    public SendMessage createMessage(Long chatId, String incomingMessage, GameSessionData tempGameData, Step selectedStep, Map<Long, Step> userCurrentStep) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        Map<RosterType, RosterTypeGameStatistic> rosterStatistics = new HashMap<>();
        List<SingleTableRowData> tableGameRows = new ArrayList<>();
        var totalGamesPlayed = tempGameData.getGameResults().size();

        Map<String, PlayerStatTableData> playerInfo = new HashMap<>();

        //TODO победы считаются неправильно
        for (var singleGameInfo : tempGameData.getGameResults()) {

            addRowToTheTableGameRows(tableGameRows, singleGameInfo);
            var isDraw = singleGameInfo.isDraw();

            for (var singleResult : singleGameInfo.getResult().entrySet()) {
                var isWinner = singleGameInfo.getWinner().equals(singleResult.getKey());
                fillTeamInfo(singleResult, rosterStatistics, isDraw, isWinner);
                fillPlayerInfo(singleResult.getKey(), singleResult.getValue().getGoals(), playerInfo);
            }

        }

        messageToSend.setParseMode(ParseMode.HTML);
        String finalMessage = "";

        finalMessage += String.format("Дата игры : %s .Всего сыгранно игр : %s\n", tempGameData.getSessionDate(), totalGamesPlayed);
        finalMessage += "Итоговая таблица по результатам игры\n";

        List<RosterTypeGameStatistic> sorterTeamStatistics = rosterStatistics.values().stream()
                .sorted(Comparator.comparing(RosterTypeGameStatistic::getPoints).reversed())
                .toList();

        List<MainTeamTable> mainRows = new ArrayList<>();
        for (var singleRosterResult : sorterTeamStatistics) {

            mainRows.add(new MainTeamTable(
                    singleRosterResult.getRosterType(),
                    singleRosterResult.getTotalGames(),
                    singleRosterResult.getGamesWon(),
                    singleRosterResult.getDraws(),
                    singleRosterResult.getGamesLost(),
                    singleRosterResult.getTotalGoalsMade(),
                    singleRosterResult.getTotalGoalsGot(),
                    singleRosterResult.getGoalDifference(),
                    singleRosterResult.getPoints()
            ));

        }

        var mainTable = new PrettyTable(MainTeamTable.FIRST_TABLE_ROW);
        mainRows.forEach(elem -> {
            try {
                mainTable.addRow(elem.getRowData());
            } catch (FieldsNotSetException | RowSizeMismatchException e) {
                throw new RuntimeException(e);
            }
        });

        finalMessage += "<pre>" + mainTable + "</pre>";

        var gamesTable = new PrettyTable(SingleTableRowData.FIRST_TABLE_ROW);
        tableGameRows.forEach(elem -> {
            try {
                gamesTable.addRow(elem.getRowData());
            } catch (FieldsNotSetException | RowSizeMismatchException e) {
                throw new RuntimeException(e);
            }
        });

        finalMessage += "Список игр:\n";
        finalMessage += "<pre>" + gamesTable + "</pre>";

        //TODO надо придумать как сделать 1 метод для создания всех таблиц
        var playersTable = new PrettyTable(PlayerStatTableData.FIRST_TABLE_ROW);

        var sortedPlayers = playerInfo.values().stream()
                .sorted(Comparator.comparing(PlayerStatTableData::getGoals).reversed())
                .toList();

        sortedPlayers.forEach(elem -> {
            try {
                playersTable.addRow(elem.getRowData());
            } catch (FieldsNotSetException | RowSizeMismatchException e) {
                throw new RuntimeException(e);
            }
        });

        finalMessage += "Статистика игроков:\n";
        finalMessage += "<pre>" + playersTable + "</pre>";

        messageToSend.setText(finalMessage);
        //TODO куда отправлять пользака?
        userCurrentStep.put(chatId, Step.START);
        return messageToSend;
    }

    private void fillPlayerInfo(RosterType key, List<SingleGoal> goals, Map<String, PlayerStatTableData> playerInfo) {
        if (CollectionUtils.isEmpty(goals)) {
            return;
        }

        Consumer<String> processPlayer = playerName -> {
            if (StringUtils.hasText(playerName)) {
                playerInfo.computeIfAbsent(playerName, p -> new PlayerStatTableData(key, p));
            }
        };


        for (SingleGoal singleGoal : goals) {
            processPlayer.accept(singleGoal.getBombardier());
            playerInfo.get(singleGoal.getBombardier()).addGoal();


            processPlayer.accept(singleGoal.getAssistant());
            if (StringUtils.hasText(singleGoal.getAssistant())) {
                playerInfo.get(singleGoal.getAssistant()).addAssist();
            }

        }
    }

//    private void fillPlayerInfo(RosterType key, List<SingleGoal> goals, Map<String, PlayerStatTableData> playerInfo) {
//
//        if (CollectionUtils.isEmpty(goals)) {
//            return;
//        }
//
//        for (var singleGoal : goals) {
//
//            PlayerStatTableData bombardier;
//
//            if (playerInfo.containsKey(singleGoal.getBombardier())) {
//                bombardier = playerInfo.get(singleGoal.getBombardier());
//            } else {
//                bombardier = new PlayerStatTableData(key, singleGoal.getBombardier());
//                playerInfo.put(singleGoal.getBombardier(), bombardier);
//            }
//
//            if (StringUtils.hasText(singleGoal.getAssistant())) {
//
//                PlayerStatTableData assistant;
//
//                if (playerInfo.containsKey(singleGoal.getAssistant())) {
//                    assistant = playerInfo.get(singleGoal.getAssistant());
//                } else {
//                    assistant = new PlayerStatTableData(key, singleGoal.getAssistant());
//                    playerInfo.put(singleGoal.getAssistant(), bombardier);
//                }
//
//                assistant.addAssist();
//
//            }
//
//            bombardier.addGoal();
//
//        }
//
//    }

    private static void fillTeamInfo(Map.Entry<RosterType, RosterSingleGameInfo> singleResult, Map<RosterType, RosterTypeGameStatistic> rosterStatistics, boolean isDraw, boolean isWinner) {
        RosterTypeGameStatistic tempRosterStat;

        if (rosterStatistics.containsKey(singleResult.getKey())) {
            tempRosterStat = rosterStatistics.get(singleResult.getKey());
        } else {
            tempRosterStat = new RosterTypeGameStatistic(singleResult.getKey());
            rosterStatistics.put(singleResult.getKey(), tempRosterStat);
        }

        if (isDraw) {
            tempRosterStat.addDraw();
        } else if (isWinner) {
            tempRosterStat.addWonGame();
        } else {
            tempRosterStat.addLostGame();
        }

        tempRosterStat.addGoalsGot(singleResult.getValue().getGoalsGot());
        tempRosterStat.addGoalsMade(singleResult.getValue().getGoals().size());

    }

    private void addRowToTheTableGameRows(List<SingleTableRowData> tableGameRows, GameResult singleGameInfo) {

        tableGameRows.add(new SingleTableRowData(
                singleGameInfo.getWinner(),
                singleGameInfo.getLooser(),
                singleGameInfo.getNumberOfGoals(singleGameInfo.getWinner()),
                singleGameInfo.getNumberOfGoals(singleGameInfo.getLooser())));

    }

//    message.setParseMode(ParseMode.HTML);
//
//    PrettyTable table = new PrettyTable(List.of("Firstname", "Lastname", "Email", "Phone"));
//        table.addRow(List.of("John", "Doe", "johndoe@nothing.com", "+2137999999"));
//        table.addRow(List.of("Jane", "Doe", "janedoe@nothin.com", "+2137999999"));
//        message.setText("<pre>"+table+"</pre>");

    /**
     message.setParseMode("HTML");
     message.setText("<pre>\n" +
     "| Tables   |      Are      |  Cool |\n" +
     "|----------|:-------------:|------:|\n" +
     "| col 1 is |  left-aligned | $1600 |\n" +
     "| col 2 is |    centered   |   $12 |\n" +
     "| col 3 is | right-aligned |    $1 |\n" +
     "</pre>");
     *
     */
}
