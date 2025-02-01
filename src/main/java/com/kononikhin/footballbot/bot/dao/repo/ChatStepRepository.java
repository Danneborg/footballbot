package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.ChatStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ChatStepRepository extends JpaRepository<ChatStep, Long> {

    @Query("SELECT cs FROM ChatStep cs WHERE cs.tgChatId = :tgChatId AND cs.isLast = true")
    Optional<ChatStep> findLastCommandByTgChatId(Long tgChatId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatStep cs SET cs.isLast = false WHERE cs.tgChatId = :tgChatId AND cs.isLast = true")
    void markPreviousCommandAsNotLast(Long tgChatId);
}