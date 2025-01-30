package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PlayerInfoToRoster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerInfo player;

    @ManyToOne
    @JoinColumn(name = "roster_id", nullable = false)
    private Roster roster;

    // Getters and Setters
}
