package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    List<GameSession> findByChatId(Long chatId);

    //TODO написать такой тест, который не позволит иметь более одной isFinished = false Для одного chatId
    GameSession findByChatIdAndIsFinished(Long chatId, boolean isFinished);

    @Modifying
    @Query("UPDATE GameSession gs SET gs.isFinished = :isFinished WHERE gs.id = :id")
    void setIsFinished(@Param("id") Long id, @Param("isFinished") Boolean isFinished);
}
