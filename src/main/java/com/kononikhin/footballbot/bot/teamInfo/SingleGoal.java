package com.kononikhin.footballbot.bot.teamInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleGoal {

    public boolean isBombardierSet = false;
    public boolean isAssistantSet = false;
    public boolean goalComplete = false;

    private String bombardier;
    private String assistant;
}
