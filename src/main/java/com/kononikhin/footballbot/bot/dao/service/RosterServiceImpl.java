package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.dao.pojo.GameSession;
import com.kononikhin.footballbot.bot.teamInfo.Roster;
import org.springframework.stereotype.Component;

@Component
public class RosterServiceImpl implements RosterService{

    @Override
    public Long saveRoster(Roster roster, GameSession gameSession) {



        return 0L;
    }
}
