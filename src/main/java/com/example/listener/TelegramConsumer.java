package com.example.listener;

import com.example.model.dto.ScheduleDto;
import com.example.model.dto.TransactionHistoryDto;
import com.example.repository.TelegramUserRepository;
import com.example.service.TelegramBotUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class TelegramConsumer {

    private static final String TELEGRAM_TOPIC = "telegram-notification";
    private static final String TELEGRAM_TOPIC_SCHEDULE = "telegram-schedule";

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramBotUserService telegramBotUserService;

    public TelegramConsumer(TelegramUserRepository telegramUserRepository, TelegramBotUserService telegramBotUserService) {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramBotUserService = telegramBotUserService;
    }

    @KafkaListener(
            topics = TELEGRAM_TOPIC,
            groupId = "notification-consumer"
    )
    void listener(ConsumerRecord<String, String> telegram) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", telegram.topic(),
                telegram.offset(), telegram.value());

        String trimmedString = telegram.value().replaceAll("^\"|\"$", "");
        String cleanedJson = trimmedString.replaceAll("\\\\", "");


        ObjectMapper objectMapper = new ObjectMapper();

        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        try {
            transactionHistoryDto = objectMapper.readValue(cleanedJson, TransactionHistoryDto.class);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            UUID userId = transactionHistoryDto.getCustomerId();
//            Check if the userId exists in your telegram_users table and get chatId
            Long chatId = telegramUserRepository.getChatIdByUserId(userId);
            if (chatId != null) {
                System.out.println("chatId: " + chatId);
                telegramBotUserService.sendTextMessage(chatId, transactionHistoryDto);

            } else {
                log.info("this userId doesn't have subscribe telegram or email notification types!");

            }
        } catch (Exception e) {
            log.error("Exception while processing Kafka record", e);
        }

    }


    @KafkaListener(
            topics = TELEGRAM_TOPIC_SCHEDULE,
            groupId = "notification-consumer"
    )
    void listenerForSchedule(ConsumerRecord<String, String> telegram) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", telegram.topic(),
                telegram.offset(), telegram.value());
        try {
            ScheduleDto scheduleDto = parseScheduleDto(telegram.value());
            if (!scheduleDto.getUserId().equals("null")) {
                UUID userId = UUID.fromString(scheduleDto.getUserId());
//            Check if the userId exists in your telegram_users table and get chatId
                Long chatId = telegramUserRepository.getChatIdByUserId(userId);
                System.out.println("chatId: " + chatId);
                telegramBotUserService.sendTextMessageSchedule(chatId, scheduleDto);

            } else {
                telegramUserRepository.findAll().forEach(user -> {
                    telegramBotUserService.sendTextMessageSchedule(user.getChatId(), scheduleDto);
                });
            }

        } catch (Exception e) {
            log.error("Exception while processing Kafka record", e);
        }

    }

    private ScheduleDto parseScheduleDto(String input) {
        String userId = null;
        String message = null;

        String[] keyValuePairs = input.substring(input.indexOf("(") + 1, input.indexOf(")")).split(",\\s*");

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                if ("userId".equals(key)) {
                    userId = value;
                } else if ("message".equals(key)) {
                    message = value;
                }
            }
        }

        if (userId != null && message != null) {
            // Assuming userId is a valid UUID string
            return new ScheduleDto(userId, message);
        } else {
            return null;
        }
    }


}
