package com.kononikhin.footballbot.bot;

import com.kononikhin.footballbot.bot.constants.Step;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class FootballBot extends TelegramLongPollingBot {

    /**
     * TODO переместить это в БД
     * Возможно при наличии базы не нужны будут попытки определить следующий шаг через if, если пользака нет в базе,
     * то просто шлем ему дефолтную информацию, а все остальные сценарии через колбэки
     */
    private final Map<Long, Step> userCurrentStep = new ConcurrentHashMap<>();

    public FootballBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {

        /**
         TODO в будущем добавить логику по ролям пользователей
         1. Для обычных давать возможность смотреть стату и подать заявку на более высокую роль
         2. Для админов игр давать возможность проводить записи игр внутри игрового дня
         */

        /**
         TODO реализовать механизм запоминания текущего статуса процесса(шага) для конкретного пользака, чтобы не начинать всегда сначала
         */
        /**
         TODO научиться рисовать клавиатуру в зависимости от доступных действий(кнопки, состав) на текущем шагея
         */
        /**
         * TODO протестировать такое, если отправить пользаку кнопки и потом остановить бота, запустить бота
         * и после этого пользак жмет кнопку, то сообщения нет, так как нет CallbackQuery, то нужно извратиться чтобы достать сообщение и команду
         * нужно протестировать такое решение
         * */

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();
            var currentUserStep = userCurrentStep.computeIfAbsent(chatId, s -> Step.START);

            var nextStep = Step.getNextStep(Step.fromConsoleCommand(message).getConsoleCommand());
            var keyboard = createKeyBoard(nextStep);
            sendMessage(chatId, keyboard);

        } else if (update.hasCallbackQuery()) {

            //TODO протестировать момент когда пользак выбирает команду из нескольких доступных и что переходы между ними осуществляются корректно
            var tempCurrentStep = Step.fromConsoleCommand(update.getCallbackQuery().getData());
            //TODO обработать вариант, когда была послана неверная команда, вернулся UNKNOWN и нужно вернуть пользака на предыдущий шаг
//            if(Step.UNKNOWN.equals(tempCurrentStep)){
//
//            }
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            var currentUserStep = userCurrentStep.computeIfAbsent(chatId, s -> Step.START);
            var nextStep = defineNextStep(chatId, tempCurrentStep.getConsoleCommand());
            var keyboard = createKeyBoard(nextStep);
            userCurrentStep.put(chatId, tempCurrentStep);
            sendMessage(chatId, keyboard);
        }

    }

    //TODO реализовать механизм сохранения текущего шага для пользака в БД
    private List<Step> defineNextStep(Long chatId, String message) {

        return Step.getNextStep(message);

    }

    //TODO доработать метод, возможно будут сложности с набором команды из игроков
    //TODO вынести в отдельный класс(через интерфейс) для создания клавиатур и элементов интерфейса чата
    private InlineKeyboardMarkup createKeyBoard(List<Step> steps) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        /*
         По информации из интернета не больше 8 кнопок в ряд и не более 100 в сумме
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
                    tempButton.setText(tempStep.getDescription());
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


    @Override
    public String getBotUsername() {
        return "kononikhin_footballbot";
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void sendMessage(Long chatId, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyMarkup(keyboard);
        //TODO добавить описание этапа
        message.setText("->>>>>>>>>>>>>>>> Скоро тут будет описание этапа <<<<<<<<<<<<<<<<-");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }
}
