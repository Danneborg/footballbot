package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfo;
import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfoToRoster;
import com.kononikhin.footballbot.bot.dao.pojo.Roster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerInfoToRosterRepository extends JpaRepository<PlayerInfoToRoster, Long> {
    List<PlayerInfoToRoster> findByPlayer(PlayerInfo player);

    List<PlayerInfoToRoster> findByRoster(Roster roster);
}
