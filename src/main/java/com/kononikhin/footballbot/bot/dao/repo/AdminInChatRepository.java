package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.AdminInChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminInChatRepository extends JpaRepository<AdminInChat, Long> {
    //TODO может быть больше 1ого чата, нужен механизм определения какой именно чат админить
    AdminInChat findAdminInChatByTgChatUserId(Long tgChatUserId);
}
