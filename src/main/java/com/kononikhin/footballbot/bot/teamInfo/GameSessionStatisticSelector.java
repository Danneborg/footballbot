package com.kononikhin.footballbot.bot.teamInfo;

import com.kononikhin.footballbot.bot.constants.Step;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

@Component
public class GameSessionStatisticSelector {
    public SendMessage createMessage(Long chatId, String incomingMessage, GameSessionData tempGameData, Step selectedStep, Map<Long, Step> userCurrentStep) {
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var totalGamesPlayed = tempGameData.getGameResults().size();

        Map<String, Integer> playerGoals;

         for(var singleGameInfo : tempGameData.getGameResults()){



         }




        return messageToSend;
    }
}
