package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerInfoToChatRepository extends JpaRepository<PlayerInfoToChat, Long> {
    List<PlayerInfoToChat> findByPlayer(PlayerInfo player);

    List<PlayerInfoToChat> findByChat(Chat cha);
}
