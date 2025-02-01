package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ChatStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tgChatId;

    @Column(nullable = false)
    private String command;

    @Column(nullable = false)
    private LocalDateTime stepTime;

    @Column(nullable = false)
    private Boolean isLast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tgChatId", referencedColumnName = "tgChatId", nullable = false, insertable = false, updatable = false)
    private Chat chat;

    public static ChatStep createNewChatStep(Long tgChatId, String command, LocalDateTime stepTime, boolean isLast) {
        var newStep = new ChatStep();
        newStep.setTgChatId(tgChatId);
        newStep.setCommand(command);
        newStep.setStepTime(stepTime);
        newStep.setIsLast(isLast);
        return newStep;
    }
}
