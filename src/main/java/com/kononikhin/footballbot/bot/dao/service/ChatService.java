package com.kononikhin.footballbot.bot.dao.service;

public interface ChatService {

    Long checkOrCreateChat(Long chatId, String chatType);

}
