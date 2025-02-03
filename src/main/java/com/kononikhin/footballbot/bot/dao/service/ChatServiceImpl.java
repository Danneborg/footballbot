package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.dao.pojo.Chat;
import com.kononikhin.footballbot.bot.dao.repo.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    //TODO надо как-то посигналить, что был создан новый чат
    public boolean checkOrCreateChat(Long tgChatId, Update update) {

        var chat = chatRepository.findChatByTgChatId(tgChatId);

        if (chat.isEmpty()) {

            var entity = new Chat();
            entity.setTgChatId(tgChatId);
            entity.setChatType(getChatType(update));

            var newChat = chatRepository.save(entity);

            return true;
        }

        return false;

    }

    private String getChatType(Update update) {

        if(update.hasMessage()) {
            return update.getMessage().getText();
        }

        if(update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }

        //TODO  может вообще быть такое? Как воспроизвести?
        return "Undefined";

    }
}
