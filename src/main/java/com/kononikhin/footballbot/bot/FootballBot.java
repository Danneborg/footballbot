package com.kononikhin.footballbot.bot;

import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.teamInfo.GameResultSelector;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionStatisticSelector;
import com.kononikhin.footballbot.bot.teamInfo.PlayersSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j

public class FootballBot extends TelegramLongPollingBot {

    //TODO перенести игроков(ники из тг) в БД
    private final static Set<String> ALL_PLAYERS = Set.of("Player1", "Player2", "Player3", "Player4", "Player5", "Player6", "Player7", "Player8", "Player9", "Player10", "Player11", "Player12", "Player13", "Player14", "Player15", "Player16", "Player17", "Player18", "Player19", "Player20", "Player21", "Player22", "Player23");

    @Autowired
    private GameResultSelector gameResultSelector;
    @Autowired
    private PlayersSelector playersSelector;
    @Autowired
    private GameSessionStatisticSelector statisticSelector;

    /**
     * TODO переместить это в БД
     * Возможно при наличии базы не нужны будут попытки определить следующий шаг через if, если пользака нет в базе,
     * то просто шлем ему дефолтную информацию, а все остальные сценарии через колбэки
     */
    private final Map<Long, Step> userCurrentStep = new ConcurrentHashMap<>();
    /**
     * TODO переместить это в БД
     * Один пользак может одновременно иметь только один игровой день
     */
    //TODO Сделать преднаполнение для тестов, чтобы не проходить весь процесс каждый раз сначала, 2 команды по 5 игровов и следующий шаг - указание счета
    private final Map<Long, GameSessionData> userRosters = new ConcurrentHashMap<>();


    public FootballBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {

        /**
         TODO в будущем добавить логику по ролям пользователей
         1. Для обычных давать возможность смотреть стату и подать заявку на более высокую роль
         2. Для админов игр давать возможность проводить записи игр внутри игрового дня
         */

        /**
         TODO реализовать механизм запоминания текущего статуса процесса(шага) для конкретного пользака, чтобы не начинать всегда сначала
         */
        /**
         TODO научиться рисовать клавиатуру в зависимости от доступных действий(кнопки, состав) на текущем шагея
         */
        /**
         * TODO протестировать такое, если отправить пользаку кнопки и потом остановить бота, запустить бота
         * и после этого пользак жмет кнопку, то сообщения нет, так как нет CallbackQuery, то нужно извратиться чтобы достать сообщение и команду
         * нужно протестировать такое решение
         * */

        Long chatId;
        String incomingMessage;

        if (update.hasMessage() && update.getMessage().hasText()) {

            chatId = update.getMessage().getChatId();
            incomingMessage = update.getMessage().getText();

        } else if (update.hasCallbackQuery()) {

            chatId = update.getCallbackQuery().getMessage().getChatId();
            incomingMessage = update.getCallbackQuery().getData();

        } else {
            chatId = 0L;
            incomingMessage = Step.UNKNOWN.getConsoleCommand();
        }

        //TODO протестировать момент когда пользак выбирает команду из нескольких доступных и что переходы между ними осуществляются корректно
        //TODO отправить сообщение, что такой команды/шага нет, и вернуть на предыдущий/стартовый шаг
        //TODO обработать вариант, когда вернулся UNKNOWN и нужно вернуть пользака на предыдущий шаг
        //TODO отработать вариант проверки, что человек не ввел руками неверный следующий шаг, допустим после START нельзя сразу выбирать составы, необходим анализ предыдущего шага
        //Предыдущего шага нет, либо пользак написал впервые, либо бот потерял кэш и данных о предыдущих шагах нет
        Step previousUserStep;
        Step selectedStep;

        if (!userCurrentStep.containsKey(chatId)) {
            previousUserStep = userCurrentStep.computeIfAbsent(chatId, s -> Step.START);
            selectedStep = previousUserStep;
        } else {
            previousUserStep = userCurrentStep.get(chatId);
            selectedStep = Step.fromConsoleCommand(incomingMessage);
        }

        //TODO возможно этот костыль можно реализовать через выбор селекторов в зависимости от шага,
        // сделать во вспомогательном классе это ветвление и просто в нем возвращать селектор в зависимости от шага,
        // а сам селектор уже вернут сообщение, пока у существующих селекторов разная сигнатура, подумать над унификацией
        //Сейчас будут костыли, но пока не знаю как вынести весь подпроцесс выбора игроков для команд красиво
        if (Step.PLAYER_SELECTION_TRIGGERS.contains(selectedStep)) {

            var tempGameData = userRosters.computeIfAbsent(chatId, s -> new GameSessionData(chatId, UUID.randomUUID()));

            var newMessage = playersSelector.createMessage(chatId, incomingMessage, tempGameData, selectedStep, ALL_PLAYERS, userCurrentStep);

            sendMessage(newMessage);

        } else if (Step.TO_RESULT_SETTING.equals(selectedStep)) {

            //TODO добавить ошибку если руками была введена команда без набранных ростеров
            var tempGameData = userRosters.computeIfAbsent(chatId, s -> new GameSessionData(chatId, UUID.randomUUID()));
            var newMessage = gameResultSelector.initiateSettingResults(chatId, tempGameData, selectedStep, userCurrentStep);
            sendMessage(newMessage);

        } else if (Step.GAME_RESULT_SET_TRIGGERS.contains(selectedStep)) {

            //TODO добавить ошибку если руками была введена команда без набранных ростеров
            var tempGameData = userRosters.computeIfAbsent(chatId, s -> new GameSessionData(chatId, UUID.randomUUID()));
            var newMessage = gameResultSelector.setGameResult(chatId, incomingMessage, tempGameData, selectedStep, userCurrentStep);
            sendMessage(newMessage);

        } else if (Step.FINISH_A_GAME_DAY.equals(selectedStep)) {

            var tempGameData = userRosters.computeIfAbsent(chatId, s -> new GameSessionData(chatId, UUID.randomUUID()));
            var message = statisticSelector.createMessage(chatId, incomingMessage, tempGameData, selectedStep, userCurrentStep);
            sendMessage(message);

        } else {

            var nextStep = Step.getNextStep(selectedStep.getConsoleCommand());

            var keyboard = Utils.createKeyBoard(nextStep);

            userCurrentStep.put(chatId, selectedStep);

            sendMessage(chatId, keyboard, selectedStep);

        }


    }

    //TODO реализовать механизм сохранения текущего шага для пользака в БД
    private List<Step> defineNextStep(Long chatId, String message) {

        return Step.getNextStep(message);

    }


    @Override
    public String getBotUsername() {
        return "kononikhin_footballbot";
    }

    private void sendMessage(SendMessage messageToSend) {
        try {
            execute(messageToSend);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void sendMessage(Long chatId, InlineKeyboardMarkup keyboard, Step selectedStep) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyMarkup(keyboard);
        message.setText(selectedStep.getStepDescription());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }
}
