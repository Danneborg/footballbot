package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Roster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSession gameSession;

    @Column(nullable = false)
    private String teamColour;

    private Boolean playedInNotFullRoster;
    // Getters and Setters
}
