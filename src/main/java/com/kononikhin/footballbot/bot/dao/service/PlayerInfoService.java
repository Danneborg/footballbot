package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfo;

import java.util.List;

public interface PlayerInfoService {

    List<PlayerInfo> getPlayersByChatId(Long chatId);

}
