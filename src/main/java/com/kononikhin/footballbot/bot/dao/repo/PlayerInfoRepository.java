package com.kononikhin.footballbot.bot.dao.repo;

import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerInfoRepository extends JpaRepository<PlayerInfo, Long> {
    PlayerInfo findByTgName(String tgName);

    List<PlayerInfo> findByIdIn(List<Long> ids);
}
