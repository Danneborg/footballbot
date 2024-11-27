package com.kononikhin.footballbot.bot.teamInfo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GoalInfo {

    private final int numberOfGoals;
    private List<SingleGoal> goals = new ArrayList<>();


    public GoalInfo(int numberOfGoals) {
        this.numberOfGoals = numberOfGoals;
    }
}
