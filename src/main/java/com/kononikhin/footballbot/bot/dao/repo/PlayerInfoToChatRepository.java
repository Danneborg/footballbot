package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.Chat;
import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfo;
import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfoToChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerInfoToChatRepository extends JpaRepository<PlayerInfoToChat, Long> {
    List<PlayerInfoToChat> findByPlayer(PlayerInfo player);

    List<PlayerInfoToChat> findByChat(Chat chat);

    List<PlayerInfoToChat> findByChatTgChatId(Long tgChatId);
}
