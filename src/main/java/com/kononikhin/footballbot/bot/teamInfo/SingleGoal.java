package com.kononikhin.footballbot.bot.teamInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleGoal {

    private boolean isBombardierSet = false;
    private boolean isAssistantSet = false;
    private boolean goalComplete = false;

    private String bombardier;
    private String assistant;
}
