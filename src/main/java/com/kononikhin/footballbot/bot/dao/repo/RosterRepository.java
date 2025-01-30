package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.GameSession;
import com.kononikhin.footballbot.bot.dao.pojo.Roster;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RosterRepository extends JpaRepository<Roster, Long> {
    List<Roster> findByGameSession(GameSession gameSession);
}
