package com.kononikhin.footballbot.bot.teamInfo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PlayerInfo {
    private Long id;
    private String tgUserName;
    private String tgVisibleName;
}
