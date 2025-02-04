package com.kononikhin.footballbot.bot.dao.service;



import com.kononikhin.footballbot.bot.dao.pojo.GameSession;
import com.kononikhin.footballbot.bot.teamInfo.Roster;

public interface RosterService {

    Long saveRoster(Roster roster, GameSession gameSession);

}
