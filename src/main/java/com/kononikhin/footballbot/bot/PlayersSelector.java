package com.kononikhin.footballbot.bot;

import com.kononikhin.footballbot.bot.constants.Step;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayersSelector {

    public SendMessage createMessage(Long chatId, String message, GameDayData gameDayData,
                                     Step rosterToFill, Step previousStep, Step nextStep,
                                     Set<String> allPlayers, Map<Long, Step> userCurrentStep) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var isTempRosterSet = gameDayData.isRosterSet(rosterToFill);

        if (!isTempRosterSet) {
            gameDayData.setRoster(rosterToFill);
        }

        //TODO добавить проверки на элемент того, что команда полная, и все выбранные команды заполнены
        var params = message.split(":");
        var playerName = params[1];

        gameDayData.addPlayerToRoster(rosterToFill, playerName);

        var isTempRosterToFillFull = gameDayData.isRosterFull(rosterToFill);



        if (isTempRosterToFillFull) {

            //TODO прописать этот шаг
            if(gameDayData.areAllRostersFull()){

            }

            gameDayData.addRosterToFull(rosterToFill);
            var keyboard = Utils.createKeyBoard(gameDayData.getNotFullRosters().stream().toList());
            messageToSend.setReplyMarkup(keyboard);
            messageToSend.setText(String.format("Состав для %s готов, выбери следующую команду :", rosterToFill.getButtonText()));
            //TODO подумать над тем как обновлять текущий шаг когда идет выбор игроков в команды, в этот момент предыдущий шаг равен следующему,
            // возможно, нужно обработать эту ситуацию в альтернативной ветке
            userCurrentStep.put(chatId, previousStep);


        } else {

            var playersToSelect = gameDayData.getNotSelectedPlayers(allPlayers);

            messageToSend.setReplyMarkup(keyboard);
            messageToSend.setText(selectedStep.getStepDescription());
        }




        return messageToSend;
    }

    private InlineKeyboardMarkup createKeyBoard(List<String> players, Step rosterToFill) {
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
                    tempButton.setText(tempStep.getButtonText());
                    tempButton.setCallbackData(tempStep.getConsoleCommand());
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
