package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SingleGameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "winner_roster_id", nullable = false)
    private Roster winnerRoster;

    @ManyToOne
    @JoinColumn(name = "looser_roster_id", nullable = false)
    private Roster looserRoster;

    @Column(nullable = false)
    private Long winnerScore;

    @Column(nullable = false)
    private Long looserScore;

    @Column(nullable = false)
    private Boolean isDraw;

    // Getters and Setters
}
