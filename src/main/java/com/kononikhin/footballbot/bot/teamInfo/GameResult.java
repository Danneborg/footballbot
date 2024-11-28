package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
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

//    private RosterType fistTeam;
//    private RosterType secondTeam;

//    private List<GoalInfo> firstTeamScore;
//    private List<GoalInfo> secondTeamScore;

    private Map<RosterType, RosterSingleGameInfo> result = new HashMap<>();

    public boolean rosterSingleGameInfoIsNotFull(RosterType roster) {

        var singleGameInfo = result.get(roster);

        return singleGameInfo.infoIsNotFull();

    }

    //TODO протестировать
    public SingleGoal getLastSingleGoalInfo(RosterType rosterType) {

        //TODO вернуть на предыдущий шаг, скорее всего ввели команду руками или был лаг бота
        if (!result.containsKey(rosterType)) {

        }

        if (CollectionUtils.isEmpty(result.get(rosterType).getGoals())) {


        }
        var tempSingleGoalInfoList = result.get(rosterType).getGoals();

        if (CollectionUtils.isEmpty(tempSingleGoalInfoList)) {
            tempSingleGoalInfoList.add(new SingleGoal());
        } else {

            if (tempSingleGoalInfoList.get(tempSingleGoalInfoList.size() - 1).isGoalComplete()) {
                tempSingleGoalInfoList.add(new SingleGoal());
            }
        }

        return tempSingleGoalInfoList.get(getResult().size() - 1);
    }

    public int getNumberOfGoals(RosterType rosterType) {
        return result.get(rosterType).getNumberOfGoals();
    }

    public void setNumberOfGoals(RosterType rosterType, int numberOfGoals) {

        //TODO вернуть на предыдущий шаг, скорее всего ввели команду руками или был лаг бота
        if (!result.containsKey(rosterType)) {

        }

        var tempGoalInfo = result.get(rosterType);

        tempGoalInfo.setNumberOfGoals(numberOfGoals);

    }

    public void setRosterTeamScoreFinished(RosterType rosterType) {

        //TODO вернуть на предыдущий шаг, скорее всего ввели команду руками или был лаг бота
        if (!result.containsKey(rosterType)) {

        }

        var tempRosterScoreInfo = result.get(rosterType);
        tempRosterScoreInfo.setSingleGameScoreSet(true);
    }

    public void setTeam(RosterType rosterType) {

        if (!result.containsKey(rosterType)) {

            result.put(rosterType, new RosterSingleGameInfo());
            if (!isFirstTeamSet) {
                isFirstTeamSet = true;
            } else {
                isSecondTeamSet = true;
            }

        }

//        if (!isFirstTeamSet) {
//            fistTeam = rosterType;
//            isFirstTeamSet = true;
//        }else {
//            secondTeam = rosterType;
//            isSecondTeamSet = true;
//        }

//        //TODO тут должен быть переход на шаг введения результатов
//        throw new IllegalArgumentException("В результат игры уже были добавлены 2 команды");
    }

}
