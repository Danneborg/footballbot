package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.RosterType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class GameResult {

    private boolean isGameFinished = false;
    private boolean isFirstTeamSet = false;
    private boolean isSecondTeamSet = false;
    private boolean isFirstTeamScoreSet = false;
    private boolean isSecondTeamScoreSet = false;
    private RosterType winner;
    private RosterType looser;
    private boolean isDraw = false;

//    private RosterType fistTeam;
//    private RosterType secondTeam;

//    private List<GoalInfo> firstTeamScore;
//    private List<GoalInfo> secondTeamScore;

    private Map<RosterType, RosterSingleGameInfo> result = new HashMap<>();

    //TODO протестировать
    public SingleGoal getLastSingleGoalInfo(RosterType rosterType) {

        //TODO вернуть на предыдущий шаг, скорее всего ввели команду руками или был лаг бота
        if (!result.containsKey(rosterType)) {

        }

        var tempSingleGoalInfoList = result.get(rosterType).getGoals();

        if (CollectionUtils.isEmpty(tempSingleGoalInfoList)) {
            tempSingleGoalInfoList.add(new SingleGoal());
        } else {

            if (tempSingleGoalInfoList.get(tempSingleGoalInfoList.size() - 1).isGoalComplete()) {
                tempSingleGoalInfoList.add(new SingleGoal());
            }
        }

        return tempSingleGoalInfoList.get(tempSingleGoalInfoList.size() - 1);
    }

    public long getNumberOfGoals(RosterType rosterType) {
        return result.get(rosterType).getNumberOfGoals();
    }

    public void setRosterTeamScoreFinished(RosterType rosterType) {

        //TODO вернуть на предыдущий шаг, скорее всего ввели команду руками или был лаг бота
        if (!result.containsKey(rosterType)) {

        }

        var tempRosterScoreInfo = result.get(rosterType);
        tempRosterScoreInfo.setSingleGameScoreSet(true);
        checkIsFinished();
    }

    private void checkIsFinished() {

        var isFinished = result.size() > 1 && result.values().stream()
                .allMatch(RosterSingleGameInfo::isSingleGameScoreSet);

        if (isFinished) {
            isGameFinished = true;
            defineWinner();
        }

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

    private void defineWinner() {

        RosterType teamOne = null;
        RosterType teamTwo = null;
        int teamOneGoals = 0;
        int teamTwoGoals = 0;

        //TODO добавить проверку, что в мапе 2 и только 2 значения
        for (Map.Entry<RosterType, RosterSingleGameInfo> entry : result.entrySet()) {
            int numGoals = entry.getValue().getGoals().size();
            if (teamOne == null) {
                teamOne = entry.getKey();
                teamOneGoals = numGoals;
            } else {
                teamTwo = entry.getKey();
                teamTwoGoals = numGoals;
            }
        }

        var tempTeamOneResult = result.get(teamOne);
        var tempTeamTwoResult = result.get(teamOne);
        tempTeamOneResult.setGoalsGot(teamTwoGoals);
        tempTeamTwoResult.setGoalsGot(teamOneGoals);

        if (teamOneGoals == teamTwoGoals) {
            isDraw = true;
            winner = teamOne;
            looser = teamTwo;
        } else if (teamOneGoals > teamTwoGoals) {
            winner = teamOne;
            looser = teamTwo;
        } else {
            winner = teamTwo;
            looser = teamOne;
        }

    }

}
