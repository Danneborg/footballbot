package com.kononikhin.footballbot.bot;

import com.kononikhin.footballbot.bot.constants.Step;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final int ELEMENTS_IN_A_ROW = 4;
    //Возможно когда-то пригодится
    private static final int MAX_ELEMENTS = 100;

    //TODO придумать что делать если послали число меньше 1. Скорее всего надо будет смотреть что было на предыдущем шаге и отправлять предыдущую информацию
    public static int defineNumberOfRows(int numberOfButtons) {

        if (numberOfButtons <= ELEMENTS_IN_A_ROW) {
            return 1;
        }

        if (numberOfButtons % ELEMENTS_IN_A_ROW == 0) {
            return numberOfButtons / ELEMENTS_IN_A_ROW;
        }

        return (numberOfButtons / ELEMENTS_IN_A_ROW) + 1;

    }

    @SneakyThrows
    public static SendMessage createMessage(Long chatId, InlineKeyboardMarkup keyboard, Step selectedStep) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyMarkup(keyboard);
        message.setText(selectedStep.getStepDescription());

        return message;
    }

    //TODO вынести в отдельный класс(через интерфейс) для создания клавиатур и элементов интерфейса чата
    public static InlineKeyboardMarkup createKeyBoard(List<Step> steps) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        /*
         По информации из интернета не больше 8 кнопок в ряд и не более 100 в сумме.
         Примем за константу 4 кнопки в ряду и не более 25 рядов.
         Учитывая, что редко играет 4 команды по 5 человек, то с запасом в сценарии с выбором игроков
         */

        var numberOfRows = Utils.defineNumberOfRows(steps.size());

        int count = 0;
        for (int i = 0; i < numberOfRows; i++) {

            List<InlineKeyboardButton> tempButtonRow = new ArrayList<>();


            for (int j = 0; j < Utils.ELEMENTS_IN_A_ROW; j++) {
                if (count < steps.size()) {

                    var tempStep = steps.get(count);

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
