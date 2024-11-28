package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.Goal;
import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.constants.Score;
import com.kononikhin.footballbot.bot.constants.Step;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GameResultSelector {

    //Первый шаг перед внесением результатов, нужно отобразить все выбранные команды и кнопку закончить день
    public SendMessage initiateSettingResults(Long chatId, GameSessionData gameSessionData,
                                              Step selectedStep, Map<Long, Step> userCurrentStep) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var listOfSteps = gameSessionData.getStepsForSetResult();
        var keyboard = Utils.createKeyBoard(listOfSteps);

        if (gameSessionData.moreAtLeastOneFinishedGame()) {
            listOfSteps.add(Step.FINISH_A_GAME_DAY);
        }

        messageToSend.setReplyMarkup(keyboard);
        messageToSend.setText(Step.SET_A_SINGLE_RESULT.getStepDescription());
        userCurrentStep.put(chatId, selectedStep);

        return messageToSend;
    }

    public SendMessage setGameResult(Long chatId, String incomingMessage, GameSessionData gameSessionData,
                                     Step selectedStep, Map<Long, Step> userCurrentStep) {

        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var params = incomingMessage.split(":");
        var tempGameResult = gameSessionData.getLastGameResult();
        var tempRosterType = RosterType.getRosterTypeFromStep(selectedStep);

        //Если параметр 1, то это название команды для которой нужно установить результат, следовательно, нужно отобразить кнопки со счетом
        if (params.length == 1) {


            tempGameResult.setTeam(tempRosterType);

            var keyboard = createKeyBoard(Score.POSSIBLE_SCORE_LIST, selectedStep);

            messageToSend.setReplyMarkup(keyboard);
            userCurrentStep.put(chatId, selectedStep);
            messageToSend.setText(selectedStep.getStepDescription());

        } else if (params.length == 2) {
            messageToSend.setText(selectedStep.getStepDescription());
            var tempTeamScore = Score.fromConsoleCommand(params[1]);
            //TODO Уходим на шаг отображения оставшейся команды и задания ее счета
            if (Score.SCORE_ZERO.equals(tempTeamScore)) {

            }
            //Есть результат больше 0, нужно начать цикл внесения результатов
            else {

                //Первый заход в цикл, нужно задать результат и отобразить кнопки с выбором бомбардира
                if (tempGameResult.getNumberOfGoals(tempRosterType) < 1) {
                    tempGameResult.setNumberOfGoals(tempRosterType, tempTeamScore.getButtonText());
                    setSelectBombardier(gameSessionData, selectedStep, tempRosterType, messageToSend);

                } else {
                    //TODO попадание сюда это ошибка, нужно понять как сюда попал пользак и вернуть его на предыдущий шаг, а лучше сделать невозможным попадание сюда
                    System.err.print("TODO попадание сюда это ошибка, нужно понять как сюда попал пользак и вернуть его на предыдущий шаг, а лучше сделать невозможным попадание сюда");
                }
            }

        }

        //Должна прийти команда типа /set_red_roster_result:/set_bombardier:<ИМЯ_ИГРОКА>
        else if (params.length == 3) {

            var goalCommand = Goal.fromConsoleCommand(params[1]);
            var lastUncompletedGoalInfo = tempGameResult.getLastSingleGoalInfo(tempRosterType);
            var players = gameSessionData.getRosterPlayers(tempRosterType);

            //TODO разобраться с ошибкой почему идет неверное вычисление и внесение результатов для голов больше 1
            if (Goal.SET_BOMBARDIER.equals(goalCommand)) {

                lastUncompletedGoalInfo.setBombardier(params[2]);
                lastUncompletedGoalInfo.setBombardierSet(true);
                //Бомбардира задали, нужно либо выбрать ассистента, либо указать, что гол без асиста
                players.remove(params[2]);
                var keyboard = createKeyBoard(new ArrayList<>(players), selectedStep, Goal.SET_ASSISTANT, true);
                messageToSend.setReplyMarkup(keyboard);
                messageToSend.setText(Goal.SET_ASSISTANT.getButtonText());

            } else if (Goal.SET_ASSISTANT.equals(goalCommand) || Goal.NO_ASSISTANT.equals(goalCommand)) {

                if (Goal.SET_ASSISTANT.equals(goalCommand)) {
                    lastUncompletedGoalInfo.setAssistant(params[2]);
                }
                lastUncompletedGoalInfo.setAssistantSet(true);
                lastUncompletedGoalInfo.setGoalComplete(true);
                //Если количество завершенных голов меньше количества общих, то повторяем цикл
                if (tempGameResult.rosterSingleGameInfoIsNotFull(tempRosterType)) {
                    setSelectBombardier(gameSessionData, selectedStep, tempRosterType, messageToSend);
                }
                //TODO добавить проверку на то, что игра в целом завершена и обе команды имеют результат
                else {
                    System.err.print("TODO реализовать проверку на конец одиночной игры или выбора команды для ");
                }

            }
            //TODO попадание сюда это ошибка, нужно понять как сюда попал пользак и вернуть его на предыдущий шаг, а лучше сделать невозможным попадание сюда
//            System.err.print("TODO попадание сюда это ошибка, нужно понять как сюда попал пользак и вернуть его на предыдущий шаг, а лучше сделать невозможным попадание сюда");
        }

        //TODO кинуть ошибку если пришло больше параметров
//
//        var setOfSteps = gameDayData.getStepsForSetResult();
//        setOfSteps.add(Step.FINISH_A_GAME_DAY);
//        var keyboard = Utils.createKeyBoard(listOfSteps);
//
//        messageToSend.setReplyMarkup(keyboard);
//        messageToSend.setText(Step.SET_A_SINGLE_RESULT.getStepDescription());
//
//        userCurrentStep.put(chatId, selectedStep);
//
        return messageToSend;
    }

    private void setSelectBombardier(GameSessionData gameSessionData, Step selectedStep, RosterType tempRosterType, SendMessage messageToSend) {
        var players = gameSessionData.getRosterPlayers(tempRosterType);

        var keyboard = createKeyBoard(new ArrayList<>(players), selectedStep, Goal.SET_BOMBARDIER, false);
        messageToSend.setReplyMarkup(keyboard);
        messageToSend.setText(Goal.SET_BOMBARDIER.getButtonText());
    }

    public List<InlineKeyboardButton> createNoAssistButton(Step step, Goal goal) {
        List<InlineKeyboardButton> tempButtonRow = new ArrayList<>();
        var tempButton = new InlineKeyboardButton();
        tempButton.setText(Goal.NO_ASSISTANT.getButtonText());
        tempButton.setCallbackData(String.format("%s:%s:%s", step.getConsoleCommand(), goal.getConsoleCommand(), Goal.NO_ASSISTANT.getConsoleCommand()));
        tempButtonRow.add(tempButton);
        return tempButtonRow;

    }

    private InlineKeyboardMarkup createKeyBoard(List<String> players, Step step, Goal goal, boolean addNoAssistantButton) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        /*
         По информации из интернета не больше 8 кнопок в ряд и не более 100 в сумме.
         Примем за константу 4 кнопки в ряду и не более 25 рядов.
         Учитывая, что редко играет 4 команды по 5 человек, то с запасом в сценарии с выбором игроков
         */

        var numberOfRows = Utils.defineNumberOfRows(players.size());

        int count = 0;
        for (int i = 0; i < numberOfRows; i++) {

            List<InlineKeyboardButton> tempButtonRow = new ArrayList<>();


            for (int j = 0; j < Utils.ELEMENTS_IN_A_ROW; j++) {
                if (count < players.size()) {

                    var tempStep = players.get(count);

                    var tempButton = new InlineKeyboardButton();
                    tempButton.setText(tempStep);
                    tempButton.setCallbackData(String.format("%s:%s:%s", step.getConsoleCommand(), goal.getConsoleCommand(), tempStep));
                    tempButtonRow.add(tempButton);

                    count++;
                } else {
                    // Важно: выход из внутреннего цикла, если элементы закончились
                    break;
                }
            }
            rowsInline.add(tempButtonRow);
        }

        if (addNoAssistantButton) {
            rowsInline.add(createNoAssistButton(step, goal));
        }

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private InlineKeyboardMarkup createKeyBoard(List<Score> possibleScoreList, Step rosterToFill) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        /*
         По информации из интернета не больше 8 кнопок в ряд и не более 100 в сумме.
         Примем за константу 4 кнопки в ряду и не более 25 рядов.
         Учитывая, что редко играет 4 команды по 5 человек, то с запасом в сценарии с выбором игроков
         */

        var numberOfRows = Utils.defineNumberOfRows(possibleScoreList.size());

        int count = 0;
        for (int i = 0; i < numberOfRows; i++) {

            List<InlineKeyboardButton> tempButtonRow = new ArrayList<>();


            for (int j = 0; j < Utils.ELEMENTS_IN_A_ROW; j++) {
                if (count < possibleScoreList.size()) {

                    var tempStep = possibleScoreList.get(count);

                    var tempButton = new InlineKeyboardButton();
                    tempButton.setText(String.valueOf(tempStep.getButtonText()));
                    tempButton.setCallbackData(String.format("%s:%s", rosterToFill.getConsoleCommand(), tempStep.getConsoleCommand()));
                    tempButtonRow.add(tempButton);

                    count++;
                } else {
                    // Важно: выход из внутреннего цикла, если элементы закончились
                    break;
                }
            }
            rowsInline.add(tempButtonRow);
        }

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

}
