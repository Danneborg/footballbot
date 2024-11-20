package com.kononikhin.footballbot.bot;

import com.kononikhin.footballbot.bot.constants.Steps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
@Slf4j
public class FootballBot extends TelegramLongPollingBot {

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
         TODO реализовать механиз запоминания текущего статуса процесса(шага) для конкретного пользака, чтобы не начинать всегда сначала
         */

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        var step = Steps.fromConsoleCommand(message);
        /**
         TODO заблочить возможность писать в чат, только кнопки с нарисованной клавиатуры
         */
        /**
         TODO научиться рисовать клавиатуру в зависимости от доступных действий(кнопки, состав) на текущем шагея
         */
        sendMessage(chatId, step.getDescription());
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
}
