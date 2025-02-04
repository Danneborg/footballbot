package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PlayerInfoToChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerInfo player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tgChatId", referencedColumnName = "tgChatId", nullable = false, insertable = false, updatable = false)
    private Chat chat;

}
