package com.kononikhin.footballbot.bot;

import com.kononikhin.footballbot.bot.constants.Step;
import com.kononikhin.footballbot.bot.dao.service.ChatService;
import com.kononikhin.footballbot.bot.dao.service.ChatStepService;
import com.kononikhin.footballbot.bot.dao.service.GameSessionService;
import com.kononikhin.footballbot.bot.selectors.GameResultSelector;
import com.kononikhin.footballbot.bot.selectors.GameSessionDataSelector;
import com.kononikhin.footballbot.bot.selectors.GameSessionStatisticSelector;
import com.kononikhin.footballbot.bot.selectors.PlayersSelector;
import com.kononikhin.footballbot.bot.teamInfo.GameSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
//TODO на старте приложения нужно создать бин, который будет грузить в inmemory хранилище все незаконченные игры и последние шаги пользователей
public class FootballBot extends TelegramLongPollingBot {

    //TODO перенести игроков(ники из тг) в БД
    private final static Set<String> ALL_PLAYERS = Set.of("Player1", "Player2", "Player3", "Player4", "Player5", "Player6", "Player7", "Player8", "Player9", "Player10", "Player11", "Player12", "Player13", "Player14", "Player15", "Player16", "Player17", "Player18", "Player19", "Player20", "Player21", "Player22", "Player23");

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatStepService chatStepService;
    @Autowired
    private GameSessionService gameSessionService;
    @Autowired
    private GameResultSelector gameResultSelector;
    @Autowired
    private PlayersSelector playersSelector;
    @Autowired
    private GameSessionStatisticSelector statisticSelector;
    @Autowired
    private GameSessionDataSelector gameSessionDataSelector;

    /**
     * TODO переместить это в БД
     * Возможно при наличии базы не нужны будут попытки определить следующий шаг через if, если пользака нет в базе,
     * то просто шлем ему дефолтную информацию, а все остальные сценарии через колбэки
     */
    private final Map<Long, Step> userCurrentStep = new ConcurrentHashMap<>();
    //Для экспериментов над отправкой последнего сообщения если полученная команда не найдена или нарушает логику
    private final Map<Long, SendMessage> userLastMessage = new ConcurrentHashMap<>();
    /**
     * TODO переместить это в БД
     * TODO Один пользак может одновременно иметь только один игровой день
     */
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
         * TODO протестировать такое, если отправить пользаку кнопки и потом остановить бота, запустить бота
         * и после этого пользак жмет кнопку, то сообщения нет, так как нет CallbackQuery, то нужно извратиться чтобы достать сообщение и команду
         * нужно протестировать такое решение
         * */

        Long chatId;
        String incomingMessage;

        //TODO нужно научиться принимать сообщения из групповых чатов и реализовать логику обработки команд оттуда
        if (update.getMessage() != null && !update.getMessage().getChat().getType().equals("private")) {

            var isNewChat = chatService.checkOrCreateChat(update.getMessage().getChatId(), update);

            if (isNewChat) {
                var warningMessage = SendMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .text("К сожалению, я еще не умею работать с типом чата" + update.getMessage().getChat().getType() + ", но скоро научусь!")
                        .build();

                sendMessage(warningMessage);
            }

            return;
        }

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

        //TODO если в середине процесса пользак "случайно" или намеренно нажал старт, то спросить его точно ли он хочет начать все сначала, если да, то очистить инфу о нем в инмемори кэше
        //TODO протестировать момент когда пользак выбирает команду из нескольких доступных и что переходы между ними осуществляются корректно
        //TODO отправить сообщение, что такой команды/шага нет, и вернуть на предыдущий/стартовый шаг
        //TODO отработать вариант проверки, что человек не ввел руками неверный следующий шаг, допустим после START нельзя сразу выбирать составы, необходим анализ предыдущего шага
        //Предыдущего шага нет, либо пользак написал впервые, либо бот потерял кэш и данных о предыдущих шагах нет
        Step previousUserStep;
        Step selectedStep;

        //TODO добавить сохранение объекта GameSessionData в БД, текущая логика с сохранением шагов в бд сломается,
        // если остановить бота на середине процесса, произойдет коллапс, так как сохранится один из промежуточных шагов, а данных для работы не будет
        if (!userCurrentStep.containsKey(chatId)) {
            previousUserStep = userCurrentStep.computeIfAbsent(chatId, s -> {
                chatService.checkOrCreateChat(chatId, update);
                return chatStepService.getLastStep(chatId, incomingMessage, null);
            });
            selectedStep = previousUserStep;
        } else {
            previousUserStep = userCurrentStep.get(chatId);
            selectedStep = Step.fromConsoleCommand(incomingMessage);
        }

        //TODO поставить заглушку на кнопку Помощь
        //TODO нельзя отправлять на "Неизвестный шаг" из кнопок сообщения, это приведет в "вечный цикл"
        //TODO возможно этот костыль можно реализовать через выбор селекторов в зависимости от шага,
        // сделать во вспомогательном классе это ветвление и просто в нем возвращать селектор в зависимости от шага,
        // а сам селектор уже вернут сообщение, пока у существующих селекторов разная сигнатура, подумать над унификацией
        //Сейчас будут костыли, но пока не знаю как вынести весь подпроцесс выбора игроков для команд красиво
        SendMessage messageToSend;


        //TODO при возврате на предыдущий шаг копируется весь текст сообщения, что при повторном вводе неверных команд
        // приводит к дублированию предупреждающий надписи надо это исправить
        /**
         * Выбранный шаг или команда не существует, отображено сообщение с предыдущего шага!
         * Выбранный шаг или команда не существует, отображено сообщение с предыдущего шага!
         * Выбранный шаг или команда не существует, отображено сообщение с предыдущего шага!
         */
        if (Step.UNKNOWN.equals(selectedStep)) {

            var lastMessage = userLastMessage.get(chatId);

            if (lastMessage == null) {
                log.error("Произошла ужасная ошибка! Пользователь попал в блок установки результатов без предыдущего сообщения!");
                var keyboard = Utils.createKeyBoard(Step.DEFAULT_BUTTON);
                messageToSend = Utils.createMessage(chatId, keyboard, selectedStep);
            } else {
                var tempMessageText = "<b>Выбранный шаг или команда не существует, отображено сообщение с предыдущего шага!</b>\n" + lastMessage.getText();
                lastMessage.setText(tempMessageText);
                messageToSend = lastMessage;
                messageToSend.setParseMode(ParseMode.HTML);
            }

        } else if (Step.PLAYER_SELECTION_TRIGGERS.contains(selectedStep)) {

            var tempGameData = userRosters.get(chatId);
            messageToSend = playersSelector.createMessage(chatId, incomingMessage, tempGameData, selectedStep, userCurrentStep);

        } else if (Step.TO_RESULT_SETTING.equals(selectedStep)) {
            //TODO добавить ошибку если руками была введена команда без набранных ростеров
            var tempGameData = userRosters.get(chatId);
            messageToSend = gameResultSelector.initiateSettingResults(chatId, tempGameData, selectedStep, userCurrentStep);

        } else if (Step.GAME_RESULT_SET_TRIGGERS.contains(selectedStep)) {

            var lastMessage = userLastMessage.get(chatId);
            //TODO написать тест на этот сценарий
            if (lastMessage == null) {

                log.error("Произошла ужасная ошибка! Пользователь попал в блок установки результатов без предыдущего сообщения!");
                var keyboard = Utils.createKeyBoard(Step.DEFAULT_BUTTON);
                messageToSend = Utils.createMessage(chatId, keyboard, selectedStep);
                messageToSend.setParseMode(ParseMode.HTML);

            } else {
                //TODO добавить ошибку если руками была введена команда без набранных ростеров
                var tempGameData = userRosters.get(chatId);
                messageToSend = gameResultSelector.setGameResult(chatId, incomingMessage, tempGameData, selectedStep, userCurrentStep, lastMessage);
            }
            //TODO ввести флаг, что текущая игровая сессия закончена и начинать новую, после отрабатывания этой кнопки, сейчас при старте новой сессии при завершении предыдущей, идут результаты из уже завершенной сессии
        } else if (Step.FINISH_A_GAME_DAY.equals(selectedStep)) {

            var tempGameData = userRosters.get(chatId);
            messageToSend = statisticSelector.createMessage(chatId, tempGameData, userCurrentStep, Step.FINISH_A_GAME_DAY.getConsoleCommand());
            messageToSend.setReplyMarkup(Utils.createKeyBoard(Step.DEFAULT_BUTTON));
            userRosters.remove(chatId);
            //TODO тут сохранить всю сессию в базу
            gameSessionService.saveGameSessionData(chatId, tempGameData);
        } else if (Step.START_GAME_DAY.equals(selectedStep)) {
            messageToSend = gameSessionDataSelector.createMessage(chatId, incomingMessage, userCurrentStep.get(chatId), userRosters);

        } else {
            //TODO надо отделить начало игрового дня от первых команд в чате, чтобы не создавать новые игровые сессии, игровая сессия создается ТОЛЬКО при нажатии Начать игровой день
            var nextStep = Step.getNextStep(selectedStep.getConsoleCommand());
            var keyboard = Utils.createKeyBoard(nextStep);
            messageToSend = Utils.createMessage(chatId, keyboard, selectedStep);
        }

        var gameSessionDataId = Optional.ofNullable(userRosters.get(chatId))
                .map(GameSessionData::getGameSessionDataDbId)
                .orElse(null);
        chatStepService.addStep(userCurrentStep, chatId, selectedStep, incomingMessage, gameSessionDataId);
        userLastMessage.put(chatId, messageToSend);
        sendMessage(messageToSend);
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

    @Override
    public void onRegister() {
        super.onRegister();

        // Регистрация команд
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Начать работу"));
        commands.add(new BotCommand("/help", "Получить справку"));
        commands.add(new BotCommand("/reset", "Прервать текущий процесс и вернуться"));

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);

        try {
            execute(setMyCommands); // Отправляем запрос Telegram API
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
