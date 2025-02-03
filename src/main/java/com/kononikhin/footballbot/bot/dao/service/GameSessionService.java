package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;


public interface GameSessionService {

    GameSessionData getUnfinishedGameSessionDataByChatId(Long chatId);

    void finishGameSession(Long gameSessionDataDbId);

    void saveGameSessionData(Long chatId, GameSessionData tempGameData);
}
