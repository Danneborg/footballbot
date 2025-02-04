package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.dao.pojo.GameSession;
import com.kononikhin.footballbot.bot.dao.repo.GameSessionRepository;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GameSessionServiceImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;

    @Override
    public GameSessionData createNewGameSessionData(Long chatId) {
        var newSession = GameSession.createNewSession(chatId);

        gameSessionRepository.save(newSession);

        return new GameSessionData(chatId, newSession.getId(), UUID.randomUUID(), newSession.getStartDate());
    }

    @Override
    public GameSession getUnfinishedGamesSessionByChatId(Long chatId) {
        return gameSessionRepository.findByChatIdAndIsFinished(chatId, false);
    }

    @Override
    public GameSessionData getUnfinishedGameSessionDataAndCreateIfDoesntExistByChatId(Long chatId) {

        var gameSession = gameSessionRepository.findByChatIdAndIsFinished(chatId, false);

        //Создаем новую игровую сессии
        if (gameSession == null) {

            var newSession = GameSession.createNewSession(chatId);

            gameSessionRepository.save(newSession);

            return new GameSessionData(chatId, newSession.getId(), UUID.randomUUID(), newSession.getStartDate());
        }


        //TODO этот шаг должен восстановить всё состояние игровой сессии из базы данных при сценарии,
        // когда игровая сессия началась, но сервер с ботом упал и потерял все inmemory данные
        return restoreGameSessionData(chatId);
    }

    @Override
    @Transactional
    public void finishGameSession(Long gameSessionDataDbId) {
        gameSessionRepository.setIsFinished(gameSessionDataDbId, true);
    }

    private GameSessionData restoreGameSessionData(Long chatId) {
        return null;
    }

    //TODO необходим механизм маппинга дто в энтити
    @Override
    @Transactional
    public void saveGameSessionData(Long chatId, GameSessionData tempGameData) {

        System.out.println();
        finishGameSession(tempGameData.getGameSessionDataDbId());



    }
}
