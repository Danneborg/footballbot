package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.SingleGameResult;
import com.kononikhin.footballbot.bot.dao.pojo.SingleGoalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SingleGoalInfoRepository extends JpaRepository<SingleGoalInfo, Long> {
    List<SingleGoalInfo> findBySingleGameResult(SingleGameResult singleGameResult);
}
