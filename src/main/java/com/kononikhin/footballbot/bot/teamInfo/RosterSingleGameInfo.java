package com.kononikhin.footballbot.bot.teamInfo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RosterSingleGameInfo {

    private boolean singleGameScoreSet = false;
    private int numberOfGoals = 0;
    private List<SingleGoal> goals = new ArrayList<>();

    public long getNumberOfGoals() {
        return goals.stream().filter(SingleGoal::isGoalComplete).count();
    }

    public boolean infoIsNotFull() {

        return numberOfGoals > goals.stream().filter(SingleGoal::isGoalComplete).count();

    }
}
