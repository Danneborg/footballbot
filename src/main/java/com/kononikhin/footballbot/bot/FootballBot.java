package com.kononikhin.footballbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@Slf4j
public class FootballBot extends TelegramLongPollingBot {

    public FootballBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info(update.toString());
    }

    @Override
    public String getBotUsername() {
        return "kononikhin_footballbot";
    }
}
