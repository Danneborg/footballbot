package com.kononikhin.footballbot.bot.dao.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ChatService {

    boolean checkOrCreateChat(Long chatId, Update update);

}
