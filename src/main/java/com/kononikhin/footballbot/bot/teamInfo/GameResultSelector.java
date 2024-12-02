package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.Goal;
import com.kononikhin.footballbot.bot.constants.RosterType;
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
        var keyboard = Utils.createKeyBoard(new ArrayList<>(listOfSteps));

        if (gameSessionData.atLeastOneFinishedGame()) {
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

        //Если передан 1 параметр, то это название команды для которой нужно установить результат, нужно отобразить кнопки выбора бомбардира или об отсутствии счета
        if (params.length == 1) {

            tempGameResult.setTeam(tempRosterType);

            var keyboard = createKeyBoard(Goal.SET_BOMBARDIER_OR_NO_GOAL, selectedStep);

            messageToSend.setReplyMarkup(keyboard);
            userCurrentStep.put(chatId, selectedStep);
            messageToSend.setText(selectedStep.getStepDescription());

        } else if (params.length == 2) {

            var goalCommand = Goal.fromConsoleCommand(params[1]);
            //TODO Уходим на шаг отображения оставшейся команды и задания ее счета
            //TODO надо как-то определить, что это была вторая команда и в таком случае отображать все команды
            if (Goal.SET_NO_GOAL.equals(goalCommand)) {

                tempGameResult.setRosterTeamScoreFinished(tempRosterType);

                var listOfTeams = gameSessionData.getStepsForSetResult();

                if(!tempGameResult.isGameFinished()){
                    messageToSend.setText("Выбери вторую сыгравшую команду");
                    listOfTeams.remove(Step.fromConsoleCommand(params[0]));
                }else {
                    messageToSend.setText("Выбери первую сыгравшую команду");
                }

                var listSteps = new ArrayList<>(listOfTeams);

                if (gameSessionData.atLeastOneFinishedGame()) {
                    listSteps.add(Step.FINISH_A_GAME_DAY);
                }

                var keyboard = Utils.createKeyBoard(listSteps);
                messageToSend.setReplyMarkup(keyboard);

                userCurrentStep.put(chatId, selectedStep);

            }
            //Есть голы есть, нужно начать цикл внесения результатов
            else {

                //TODO нельзя вносить ассистента до внесения бомбардира, отобразить список игроков команды и указать, что будет произведен выбор бомбардира
                if (Goal.SET_ASSISTANT.equals(goalCommand)) {

                }

                boolean addNoGoalButton = false;

                //TODO нужно поменять описание шага, чтобы сразу было видно, что можно указать отсутствие голов
                if(Goal.NO_ASSISTANT.equals(goalCommand)) {
                    addNoGoalButton = true;
                }

                //TODO сюда напрашивается проверка на ошибку, чутье подсказывает, что надо, но конкретики пока нет
                var players = gameSessionData.getRosterPlayers(tempRosterType);
                var keyboard = createKeyBoard(new ArrayList<>(players), selectedStep, Goal.SET_BOMBARDIER, false, addNoGoalButton);

                messageToSend.setReplyMarkup(keyboard);
                messageToSend.setText(String.format("%s для команды : %s", Goal.SET_BOMBARDIER.getButtonText(), tempRosterType.getColour()));
            }

        }

        //Должна прийти команда типа /set_red_roster_result:/set_bombardier:<ИМЯ_ИГРОКА>
        else if (params.length == 3) {

            var goalCommand = Goal.fromConsoleCommand(params[1]);
            var lastUncompletedGoalInfo = tempGameResult.getLastSingleGoalInfo(tempRosterType);
            var players = gameSessionData.getRosterPlayers(tempRosterType);

            if (Goal.SET_BOMBARDIER.equals(goalCommand)) {

                lastUncompletedGoalInfo.setBombardier(params[2]);
                lastUncompletedGoalInfo.setBombardierSet(true);
                //Бомбардира задали, нужно либо выбрать ассистента, либо указать, что гол без асиста
                players.remove(params[2]);
                var keyboard = createKeyBoard(new ArrayList<>(players), selectedStep, Goal.SET_ASSISTANT, true, false);
                messageToSend.setReplyMarkup(keyboard);
                messageToSend.setText(String.format("%s для команды : %s.\nИли укажи, что ассистента нет.", Goal.SET_ASSISTANT.getButtonText(), tempRosterType.getColour()));
                userCurrentStep.put(chatId, selectedStep);
            } else if (Goal.SET_ASSISTANT.equals(goalCommand) || Goal.NO_ASSISTANT.equals(goalCommand)) {

                if (Goal.SET_ASSISTANT.equals(goalCommand)) {
                    lastUncompletedGoalInfo.setAssistant(params[2]);
                }
                lastUncompletedGoalInfo.setAssistantSet(true);
                lastUncompletedGoalInfo.setGoalComplete(true);

                //Весь цикл нужно повторять пока не придет команда Goal.SET_NO_GOAL, следовательно, отображаем кнопки выбрать бомбардира или выбрать другую команду
                //TODO надо как-то определить, что это была вторая команда и отобразить кнопку TO_RESULT_SETTING
                var keyboard = createKeyBoard(Goal.SET_BOMBARDIER_OR_NO_GOAL, selectedStep);

                messageToSend.setReplyMarkup(keyboard);
                userCurrentStep.put(chatId, selectedStep);
                messageToSend.setText(String.format("Гол для команды %s добавлен, всего голов %s.\n Добавь еще гол или выбери вторую команду",
                        tempRosterType.getColour(), tempGameResult.getNumberOfGoals(tempRosterType)));

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

    public List<InlineKeyboardButton> createNoAssistOrNoGoalOrAssistButton(Step step, Goal goal) {
        List<InlineKeyboardButton> tempButtonRow = new ArrayList<>();
        var tempButton = new InlineKeyboardButton();
        tempButton.setText(goal.getButtonText());
        tempButton.setCallbackData(String.format("%s:%s", step.getConsoleCommand(), goal.getConsoleCommand()));
        tempButtonRow.add(tempButton);
        return tempButtonRow;

    }

    private InlineKeyboardMarkup createKeyBoard(List<String> players, Step step, Goal goal, boolean addNoAssistantButton, boolean addNoGoalButton) {
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
            rowsInline.add(createNoAssistOrNoGoalOrAssistButton(step, Goal.NO_ASSISTANT));
        }

        if (addNoGoalButton) {
            rowsInline.add(createNoAssistOrNoGoalOrAssistButton(step, Goal.SET_NO_GOAL));
        }

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private InlineKeyboardMarkup createKeyBoard(List<Goal> bombardierOrNoGoal, Step rosterToFill) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        /*
         По информации из интернета не больше 8 кнопок в ряд и не более 100 в сумме.
         Примем за константу 4 кнопки в ряду и не более 25 рядов.
         Учитывая, что редко играет 4 команды по 5 человек, то с запасом в сценарии с выбором игроков
         */

        var numberOfRows = Utils.defineNumberOfRows(bombardierOrNoGoal.size());

        int count = 0;
        for (int i = 0; i < numberOfRows; i++) {

            List<InlineKeyboardButton> tempButtonRow = new ArrayList<>();


            for (int j = 0; j < Utils.ELEMENTS_IN_A_ROW; j++) {
                if (count < bombardierOrNoGoal.size()) {

                    var tempStep = bombardierOrNoGoal.get(count);

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
