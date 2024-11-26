package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.Step;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

@Component
public class GameResultSelector {

    public SendMessage setGameResult(Long chatId, String incomingMessage, GameDayData gameDayData,
                                     Step selectedStep, Map<Long, Step> userCurrentStep) {

        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var listOfSteps = gameDayData.getAllRostersTypes();
        listOfSteps.add(Step.FINISH_A_GAME_DAY);
        var keyboard = Utils.createKeyBoard(listOfSteps);
        messageToSend.setReplyMarkup(keyboard);

        messageToSend.setText(Step.SET_A_SINGLE_RESULT.getStepDescription());

        userCurrentStep.put(chatId, selectedStep);

        return messageToSend;
    }

}
