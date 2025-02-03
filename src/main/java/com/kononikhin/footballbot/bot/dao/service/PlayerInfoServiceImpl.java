package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfo;
import com.kononikhin.footballbot.bot.dao.repo.PlayerInfoRepository;
import com.kononikhin.footballbot.bot.dao.repo.PlayerInfoToChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlayerInfoServiceImpl implements PlayerInfoService{

    private final PlayerInfoRepository playerInfoRepository;
    private final PlayerInfoToChatRepository playerInfoToChatRepository;



    @Override
    public List<PlayerInfo> getPlayersByChatId(Long chatId) {
        return List.of();
    }
}
