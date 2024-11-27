package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO реализовать хранение результатов одиночной игры, кто выиграл/проиграл, кто забил и отдал пас
@Setter
@Getter
public class GameResult {

    private boolean isGameFinished = false;
    private boolean isFirstTeamSet = false;
    private boolean isSecondTeamSet = false;
    private boolean isFirstTeamScoreSet = false;
    private boolean isSecondTeamScoreSet = false;

    private RosterType fistTeam;
    private RosterType secondTeam;

    private List<GoalInfo> firstTeamScore;
    private List<GoalInfo> secondTeamScore;

    private Map<RosterType, List<GoalInfo>> result = new HashMap<>();

    public void setNumberOfGoals(RosterType rosterType, int numberOfGoals) {

        if(rosterType.equals(fistTeam)){
            firstTeamScore = new ArrayList<>(numberOfGoals);
        }

        if(rosterType.equals(secondTeam)){
            secondTeamScore = new ArrayList<>(numberOfGoals);
        }

//        //TODO тут должен быть переход на шаг введения результатов
//        throw new IllegalArgumentException("В результат игры уже были добавлены 2 команды, переданный тип команды : "+ rosterType);
    }

    public void setTeam(RosterType rosterType) {

        if (!isFirstTeamSet) {
            fistTeam = rosterType;
            isFirstTeamSet = true;
        }

        if (!isSecondTeamSet) {
            secondTeam = rosterType;
            isSecondTeamSet = true;
        }

//        //TODO тут должен быть переход на шаг введения результатов
//        throw new IllegalArgumentException("В результат игры уже были добавлены 2 команды");
    }

}
