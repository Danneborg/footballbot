package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.dao.pojo.ChatStep;
import com.kononikhin.footballbot.bot.dao.pojo.GameSession;
import com.kononikhin.footballbot.bot.dao.repo.ChatStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatStepServiceImpl implements ChatStepService {

    private final ChatStepRepository chatStepRepository;

    private void addNewCommand(Long tgChatId, String command, Long gameSessionId) {
        chatStepRepository.markPreviousCommandAsNotLast(tgChatId);
        var newStep = ChatStep.createNewChatStep(tgChatId, command, LocalDateTime.now(), true, gameSessionId);
        chatStepRepository.save(newStep);
    }

    private Optional<ChatStep> getLastCommand(Long tgChatId) {
        return chatStepRepository.findLastCommandByTgChatId(tgChatId);
    }

    //TODO пока тестовый режим, нужно добавить addNewCommand во все месте где идет манипуляция с inmemory хранением последнего шага чата
    public Step getLastStep(Long tgChatId, String command, Long gaeSessionId) {
        var lastStepOpt = getLastCommand(tgChatId);

        if (lastStepOpt.isEmpty()) {
            addNewCommand(tgChatId, command, gaeSessionId);
            return Step.START_NEVER_SPOKE;
        }

        return Step.fromConsoleCommand(lastStepOpt.get().getCommand());

    }

    @Override
    @Transactional
    public void addStep(Map<Long, Step> usersToStep, Long tgChatId, Step step, String consoleCommand, Long gameSessionId) {
        chatStepRepository.markPreviousCommandAsNotLast(tgChatId);
        var newStep = ChatStep.createNewChatStep(tgChatId, consoleCommand, LocalDateTime.now(), true, gameSessionId);
        chatStepRepository.save(newStep);
        usersToStep.put(tgChatId, step);
    }

}
