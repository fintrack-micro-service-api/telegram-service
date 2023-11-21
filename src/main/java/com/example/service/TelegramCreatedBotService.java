package com.example.service;

import com.example.model.dto.TelegramCreatedBot;
import com.example.request.TelegramCreatedBotRequest;

import java.util.List;

public interface TelegramCreatedBotService {

    TelegramCreatedBot updateBot(TelegramCreatedBotRequest telegramCreatedBotRequest);
    List<TelegramCreatedBot> getBots();

//    TelegramCreatedBot createBot(TelegramCreatedBotRequest telegramCreatedBotRequest);
//    TelegramCreatedBot getBotByBotUsername(String botUsername);
//
//    Optional<TelegramCreatedBot> getBotByBotId(Long botId);
//
//    TelegramCreatedBot updateBotByBotUsername(String botUsername, TelegramCreatedBotRequest telegramCreatedBotRequest);
//    void deleteBotById(Long botId);

}