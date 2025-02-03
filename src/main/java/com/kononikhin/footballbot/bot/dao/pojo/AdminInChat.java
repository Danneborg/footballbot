package com.kononikhin.footballbot.bot.dao.pojo;

import jakarta.persistence.*;
import lombok.Data;

//TODO подумать как можно автоматизировать процесс назначения админа на чат
@Entity
@Data
public class AdminInChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tgChatUserId;

    @Column(nullable = false)
    private Long tgGroupChatId;

}
