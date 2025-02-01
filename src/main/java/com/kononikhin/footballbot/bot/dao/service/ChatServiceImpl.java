package com.kononikhin.footballbot.bot.dao.service;

import com.kononikhin.footballbot.bot.dao.pojo.Chat;
import com.kononikhin.footballbot.bot.dao.repo.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public Long checkOrCreateChat(Long tgChatId, String chatType) {

        var chat = chatRepository.findChatByTgChatId(tgChatId);

        if (chat.isEmpty()) {

            var entity = new Chat();
            entity.setTgChatId(tgChatId);
            entity.setChatType(chatType);

            var newChat = chatRepository.save(entity);

            return newChat.getTgChatId();
        }

        return tgChatId;


    }
}
