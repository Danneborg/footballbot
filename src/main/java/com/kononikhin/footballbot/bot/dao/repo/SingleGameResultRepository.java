package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.GameSession;
import com.kononikhin.footballbot.bot.dao.pojo.SingleGameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SingleGameResultRepository extends JpaRepository<SingleGameResult, Long> {
    List<SingleGameResult> findByGameSession(GameSession gameSession);
}
