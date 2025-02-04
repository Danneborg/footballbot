package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.dao.pojo.GameSession;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;


public interface GameSessionService {

    GameSessionData getUnfinishedGameSessionDataAndCreateIfDoesntExistByChatId(Long chatId);

    GameSessionData createNewGameSessionData(Long chatId);

    GameSession getUnfinishedGamesSessionByChatId(Long chatId);

    void finishGameSession(Long gameSessionDataDbId);

    void saveGameSessionData(Long chatId, GameSessionData tempGameData);
}
