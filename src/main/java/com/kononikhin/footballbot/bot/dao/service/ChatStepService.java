package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.constants.Step;

import java.util.Map;

public interface ChatStepService {

    Step getLastStep(Long tgChatId, String command);

    void addStep(Map<Long, Step> usersToStep, Long tgChatId, Step step, String consoleCommand);
}
