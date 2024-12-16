package com.kononikhin.footballbot.bot.dto;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.Getter;

@Getter
public class RosterTypeGameStatistic {

    private static final int VICTORY_POINTS = 3;
    private static final int DRAW_POINTS = 1;
    private final RosterType rosterType;

    private long gamesWon;
    private long gamesLost;
    private long draws;
    private long totalGoalsMade;
    private long totalGoalsGot;

    public RosterTypeGameStatistic(RosterType rosterType) {
        this.rosterType = rosterType;
    }

    public void addWonGame() {
        gamesWon++;
    }

    public void addLostGame() {
        gamesLost++;
    }

    public void addDraw() {
        draws++;
    }

    public void addGoalsMade(long goalsMade) {
        totalGoalsMade += goalsMade;
    }

    public void addGoalsGot(long goalsGot) {
        totalGoalsGot += goalsGot;
    }

    public long getPoints() {
        return gamesWon * VICTORY_POINTS + draws * DRAW_POINTS;
    }

    public long getTotalGames() {
        return gamesWon + gamesLost + draws;
    }

    public long getGoalDifference() {
        return totalGoalsMade - totalGoalsGot;
    }
}
