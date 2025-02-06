package com.kononikhin.footballbot.bot.selectors;

import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.dao.service.GameSessionService;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GameSessionDataSelector {

    private final GameSessionService gameSessionService;

    public SendMessage createMessage(Long chatId, String incomingMessage, Step userCurrentStep, Map<Long, GameSessionData> userRosters) {

        var unfinishedGameSessionData = gameSessionService.getUnfinishedGamesSessionByChatId(chatId);

        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var text = "";

        //TODO нужно как-то посигналить пользаку, что у него есть незаконченная сессия и действительно ли он хочет
        // завершить текущую сессию и начать новую. Сейчас для сокращения выхода в MVP действующая сессия заканчивается по умолчанию
        if (unfinishedGameSessionData != null) {
            gameSessionService.finishGameSession(unfinishedGameSessionData.getId());
            var keyboard = Utils.createKeyBoard(Step.BUTTON_START_LIST);
            messageToSend.setText("У тебя была незакрытая сессия. Она была закрыта при попадании в это шаг.\nВ будущем это будет исправлено.\nОтправляю тебя на стартовый шаг");
            messageToSend.setReplyMarkup(keyboard);
        }else {
            var newGameSessionData = gameSessionService.createNewGameSessionData(chatId);
            userRosters.put(chatId, newGameSessionData);
            var keyboard = Utils.createKeyBoard(Step.ROSTERS);
            messageToSend.setText(Step.START.getStepDescription());
            messageToSend.setReplyMarkup(keyboard);
        }

        return messageToSend;
    }

}
