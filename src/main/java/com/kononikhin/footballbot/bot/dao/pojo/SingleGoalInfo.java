package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SingleGoalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "single_game_result_id", nullable = false)
    private SingleGameResult singleGameResult;

    @ManyToOne
    @JoinColumn(name = "roster_id", nullable = false)
    private Roster roster;

    @ManyToOne
    @JoinColumn(name = "bombardier_id", nullable = false)
    private PlayerInfo bombardier;

    @ManyToOne
    @JoinColumn(name = "assistant_id")
    private PlayerInfo assistant;

    // Getters and Setters
}