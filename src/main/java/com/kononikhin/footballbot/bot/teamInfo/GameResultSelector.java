package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.Utils;
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
    public SendMessage initiateSettingResults(Long chatId, GameDayData gameDayData,
                                              Step selectedStep, Map<Long, Step> userCurrentStep) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var listOfSteps = gameDayData.getStepsForSetResult();
        var keyboard = Utils.createKeyBoard(listOfSteps);

        if (gameDayData.moreAtLeastOneFinishedGame()) {
            listOfSteps.add(Step.FINISH_A_GAME_DAY);
        }

        messageToSend.setReplyMarkup(keyboard);
        messageToSend.setText(Step.SET_A_SINGLE_RESULT.getStepDescription());
        userCurrentStep.put(chatId, selectedStep);

        return messageToSend;
    }

    public SendMessage setGameResult(Long chatId, String incomingMessage, GameDayData gameDayData,
                                     Step selectedStep, Map<Long, Step> userCurrentStep) {

        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var params = incomingMessage.split(":");
        var tempGameResult = gameDayData.getLastGameResult();
        var tempRosterType = RosterType.getRosterTypeFromStep(selectedStep);

        //Если параметр 1, то это название команды для которой нужно установить результат, следовательно, нужно отобразить кнопки со счетом
        if (params.length == 1) {


            tempGameResult.setTeam(tempRosterType);

            var keyboard = createKeyBoard(Score.POSSIBLE_SCORE_LIST, selectedStep);

            messageToSend.setReplyMarkup(keyboard);
            messageToSend.setText(selectedStep.getStepDescription());

            userCurrentStep.put(chatId, selectedStep);
        }

        if (params.length == 2) {
            //Нужно войти 
            var tempTeamScore = Score.fromConsoleCommand(params[1]);
            //Уходим на шаг отображения оставшейся команды и задания ее счета
            if (Score.SCORE_ZERO.equals(tempTeamScore)) {

            }
            //Есть результат больше 0, нужно вносить забивших
            else {

                //TODO войти в цикл внесения бомбардиров и ассистентов
                tempGameResult.setNumberOfGoals(tempRosterType, tempTeamScore.getButtonText());


            }

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
