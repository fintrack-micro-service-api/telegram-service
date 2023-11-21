package com.example.config;

import com.example.service.TelegramBotUserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBotInitializer {

    private final TelegramBotUserService telegramBotUserService;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotInitializer.class);

    public TelegramBotInitializer(TelegramBotUserService telegramBotUserService) {
        this.telegramBotUserService = telegramBotUserService;
    }

//    @EventListener(ContextRefreshedEvent.class)
    @PostConstruct
    public void init() {
        try {
//            telegramBotUserService.clearWebhook();

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            try {
                telegramBotsApi.registerBot(telegramBotUserService);
            } catch (TelegramApiException e) {
                throw new RuntimeException("Error: " + e.getMessage());
//                e.printStackTrace();
            }
            logger.info("Telegram bot registered successfully.");
        } catch (TelegramApiRequestException e) {
            if (e.getErrorCode() == 409) {
                logger.error("Conflict error while registering Telegram bot. Retrying...");
            } else {
                logger.error("Other Telegram API request exception: " + e.getMessage(), e);
            }
        } catch (TelegramApiException e) {
            logger.error("Error registering Telegram bot", e);
        }
    }


}
