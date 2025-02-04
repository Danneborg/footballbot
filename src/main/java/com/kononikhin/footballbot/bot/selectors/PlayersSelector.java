package com.kononikhin.footballbot.bot.selectors;

import com.kononikhin.footballbot.bot.Utils;
import com.kononikhin.footballbot.bot.constants.RosterType;
import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.dao.pojo.PlayerInfoToChat;
import com.kononikhin.footballbot.bot.dao.repo.AdminInChatRepository;
import com.kononikhin.footballbot.bot.dao.repo.PlayerInfoRepository;
import com.kononikhin.footballbot.bot.dao.repo.PlayerInfoToChatRepository;
import com.kononikhin.footballbot.bot.dao.service.ChatStepService;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlayersSelector {

    private final static int MINIMUM_ROSTERS_TO_PLAY = 2;
    private final ChatStepService chatStepService;
    private final AdminInChatRepository adminInChatRepository;
    private final PlayerInfoToChatRepository playerInfoToChatRepository;
    private final PlayerInfoRepository playerInfoRepository;


    public SendMessage createMessage(Long chatId, String incomingMessage, GameSessionData gameSessionData,
                                     Step rosterToFill, Map<Long, Step> userCurrentStep) {

        //TODO не грузятся игроки из привязанного чата
        if (!gameSessionData.isPlayersLoaded()) {
            loadPlayers(gameSessionData);
        }

        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(chatId);

        var rosterType = RosterType.getRosterTypeFromStep(rosterToFill);

        var isTempRosterSet = gameSessionData.isRosterSet(rosterType);

        if (!isTempRosterSet) {
            gameSessionData.setRoster(rosterType);
        }

        var params = incomingMessage.split(":");

        //Если нет 2ого параметра в виде имени игрока, значит пользак нажал на кнопку первый раз
        if (params.length > 1) {
            var playerName = params[1];
            gameSessionData.addPlayerToRoster(rosterType, playerName, gameSessionData.getPlayerInfoByTgName(playerName));
        }

        var isTempRosterToFillFull = gameSessionData.isRosterFull(rosterType);

        if (isTempRosterToFillFull) {

            //TODO нужно обработать ситуацию когда не хватает игроков в одной команде и добавить возможность начать в неполных составах
            if (gameSessionData.isGameDayReadyToStart(MINIMUM_ROSTERS_TO_PLAY)) {
                messageToSend.setParseMode(ParseMode.HTML);

                var listOfSteps = gameSessionData.getNotFullRosters();
                listOfSteps.add(Step.TO_RESULT_SETTING);
                var keyboard = Utils.createKeyBoard(listOfSteps);
                messageToSend.setReplyMarkup(keyboard);

                messageToSend.setText(String.format("Состав для <b>%s</b> готов. Набрано <b>%s</b> полных команд.\n" +
                                "<i>Можно начинать играть или набрать еще 1 команду.</i>",
                        rosterToFill.getButtonText(), gameSessionData.getNumberOfFullRosters()));

            } else {

                var keyboard = Utils.createKeyBoard(gameSessionData.getNotFullRosters());
                messageToSend.setReplyMarkup(keyboard);
                messageToSend.setText(String.format("Состав для %s готов, выбери следующую команду :", rosterToFill.getButtonText()));
            }


        } else {

            var playersToSelect = gameSessionData.getNotSelectedPlayers(gameSessionData.getLoadedPlayers());
            var keyboard = createKeyBoard(new ArrayList<>(playersToSelect), rosterToFill);
            messageToSend.setReplyMarkup(keyboard);

            var header = String.format("Выбранные игроки : %s", gameSessionData.getRosterPlayers(rosterType));
            var footer = String.format("Для команды %s осталось выбрать %s игроков", rosterToFill.getButtonText(), GameSessionData.ROSTER_SIZE - gameSessionData.getRosterSize(rosterType));

            messageToSend.setText(header + "\n" + footer);
            messageToSend.setParseMode(ParseMode.HTML);

        }

        return messageToSend;
    }

    //TODO Объединить эти методы с методами из GameResultSelector
    private InlineKeyboardMarkup createKeyBoard(List<String> players, Step rosterToFill) {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        /*
         По информации из интернета не больше 8 кнопок в ряд и не более 100 в сумме.
         Примем за константу 4 кнопки в ряду и не более 25 рядов.
         Учитывая, что редко играет 4 команды по 5 человек, то с запасом в сценарии с выбором игроков
         */

        var numberOfRows = Utils.defineNumberOfRows(players.size());

        int count = 0;
        for (int i = 0; i < numberOfRows; i++) {

            List<InlineKeyboardButton> tempButtonRow = new ArrayList<>();


            for (int j = 0; j < Utils.ELEMENTS_IN_A_ROW; j++) {
                if (count < players.size()) {

                    var tempStep = players.get(count);

                    var tempButton = new InlineKeyboardButton();
                    tempButton.setText(tempStep);
                    tempButton.setCallbackData(String.format("%s:%s", rosterToFill.getConsoleCommand(), tempStep));
                    tempButtonRow.add(tempButton);

                    count++;
                } else {
                    // Важно: выход из внутреннего цикла, если элементы закончились
                    break;
                }
            }
            rowsInline.add(tempButtonRow);
        }

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private void loadPlayers(GameSessionData gameSessionData) {

        var admin = adminInChatRepository.findAdminInChatByTgChatUserId(gameSessionData.getChatId());

        //TODO очень плохая ошибка, вернуть на старт, убрать админство, понять как воспроизвести
        if (admin == null) {
            return;
        }

        //TODO может быть больше 1, но это будет позже

        //TODO добавить проверку, что игроки вообще есть
        var playerIds = playerInfoToChatRepository.findByChatTgChatId(admin.getTgGroupChatId())
                .stream().map(PlayerInfoToChat::getId)
                .collect(Collectors.toList());

        //TODO что-то сделать с тем случаем, когда игроков нет, идеально не пустить админа сюда, так как не набрался состав
        if(CollectionUtils.isEmpty(playerIds)) {
            return;
        }

        gameSessionData
                .setListOfPlayers(new HashSet<>(playerInfoRepository.findByIdIn(playerIds)));

        gameSessionData.setPlayersLoaded(true);
    }
}
