package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.constants.Step;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class PlayersSelector {

    //TODO добавить проверку на заполнение минимального количества команд для игры
    private final static int MINIMUM_ROSTERS_TO_PLAY = 2;

    public SendMessage createMessage(Long chatId, String incomingMessage, GameDayData gameDayData,
                                     Step rosterToFill, Set<String> allPlayers, Map<Long, Step> userCurrentStep) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var rosterType = RosterType.getRosterTypeFromStep(rosterToFill);

        var isTempRosterSet = gameDayData.isRosterSet(rosterType);

        if (!isTempRosterSet) {
            gameDayData.setRoster(rosterType);
        }

        //TODO добавить проверки на элемент того, что команда полная и все выбранные команды заполнены
        var params = incomingMessage.split(":");

        //Если нет 2ого параметра в виде имени игрока, значит пользак нажал на кнопку первый раз
        if (params.length > 1) {
            var playerName = params[1];
            gameDayData.addPlayerToRoster(rosterType, playerName);
        }

        var isTempRosterToFillFull = gameDayData.isRosterFull(rosterType);

        if (isTempRosterToFillFull) {

            //TODO прописать этот шаг
            //TODO добавить проверку, что набрано минимальное количество полных команд для игры и добавить кнопку перехода в процесс записи результатов
            //TODO нужно обработать ситуацию когда не хватает игроков в одной команде и добавить возможность начать в неполных составах
            if (gameDayData.isGameDayReadyToStart(MINIMUM_ROSTERS_TO_PLAY)) {
                messageToSend.setParseMode(ParseMode.HTML);

                var listOfSteps = gameDayData.getNotFullRosters();
                listOfSteps.add(Step.TO_RESULT_SETTING);
                var keyboard = Utils.createKeyBoard(listOfSteps);
                messageToSend.setReplyMarkup(keyboard);

                messageToSend.setText(String.format("Состав для <b>%s</b> готов. Набрано полных команд <b>%s.</b>\n" +
                                "<i>Можно начинать играть или набрать еще 1 команду.</i>",
                        rosterToFill.getButtonText(), gameDayData.getNumberOfFullRosters()));

                userCurrentStep.put(chatId, rosterToFill);

            } else {

                var keyboard = Utils.createKeyBoard(gameDayData.getNotFullRosters());
                messageToSend.setReplyMarkup(keyboard);
                messageToSend.setText(String.format("Состав для %s готов, выбери следующую команду :", rosterToFill.getButtonText()));
                //TODO подумать над тем как обновлять текущий шаг когда идет выбор игроков в команды, в этот момент предыдущий шаг равен следующему,
                // возможно, нужно обработать эту ситуацию в альтернативной ветке
                userCurrentStep.put(chatId, rosterToFill);

            }


        } else {

            var playersToSelect = gameDayData.getNotSelectedPlayers(allPlayers);
            var keyboard = createKeyBoard(new ArrayList<>(playersToSelect), rosterToFill);
            messageToSend.setReplyMarkup(keyboard);
            messageToSend.setText(String.format("Для команды %s осталось выбрать %s игроков", rosterToFill.getButtonText(), GameDayData.ROSTER_SIZE - gameDayData.getRosterSize(rosterType)));

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
                    tempButton.setText(tempStep);
                    tempButton.setCallbackData(String.format("%s:%s", rosterToFill.getConsoleCommand(), tempStep));
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
