package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean isFinished;

    // Getters and Setters

    public static GameSession createNewSession(Long chatId) {
        var newSession = new GameSession();
        newSession.setChatId(chatId);
        newSession.setStartDate(LocalDateTime.now());
        return newSession;
    }

}