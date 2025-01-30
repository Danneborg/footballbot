package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PlayerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tgName;

    private String tgVisibleName;

    // Getters and Setters
}