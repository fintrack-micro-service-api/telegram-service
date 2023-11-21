package com.example.service.implementation;

import com.example.model.dto.TelegramCreatedBot;
import com.example.repository.TelegramCreatedBotRepository;
import com.example.request.TelegramCreatedBotRequest;
import com.example.service.TelegramCreatedBotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TelegramCreatedBotServiceImp implements TelegramCreatedBotService {

    private final TelegramCreatedBotRepository telegramCreatedBotRepository;

    @Override
    public TelegramCreatedBot updateBot(TelegramCreatedBotRequest telegramCreatedBotRequest) {
        List<Long> botIdAtIndexZero = telegramCreatedBotRepository.getBotIdAtIndexZero();

        if (botIdAtIndexZero.isEmpty()) {
            throw new EntityNotFoundException("Bot not found with botId stored in index 0.");
        }

        Long botId = botIdAtIndexZero.get(0);

        Optional<TelegramCreatedBot> optionalBot = telegramCreatedBotRepository.findById(botId);

        if (optionalBot.isEmpty()) {
            throw new EntityNotFoundException("Bot not found with botId: " + botId);
        }

        TelegramCreatedBot telegramCreatedBot = optionalBot.get();

        if (telegramCreatedBotRequest.getBotUsername().isEmpty() && telegramCreatedBotRequest.getBotUsername().isBlank() ||
                telegramCreatedBotRequest.getBotToken().isEmpty() && telegramCreatedBotRequest.getBotToken().isBlank() ||
                telegramCreatedBotRequest.getBotLink().isEmpty() && telegramCreatedBotRequest.getBotLink().isBlank()) {
            throw new IllegalArgumentException("Invalid request. BotUsername, BotToken, and BotLink are required.");
        }

        telegramCreatedBot.setBotUsername(telegramCreatedBotRequest.getBotUsername());
        telegramCreatedBot.setBotToken(telegramCreatedBotRequest.getBotToken());
        telegramCreatedBot.setBotLink(telegramCreatedBotRequest.getBotLink());

        return telegramCreatedBotRepository.save(telegramCreatedBot);
    }

    @Override
    public List<TelegramCreatedBot> getBots() {
        List<TelegramCreatedBot> telegramCreatedBots = telegramCreatedBotRepository.findAll();
        return telegramCreatedBots;
    }


    //    @Override
//    public TelegramCreatedBot createBot(TelegramCreatedBotRequest telegramCreatedBotRequest) {
//        TelegramCreatedBot telegramCreatedBot = new TelegramCreatedBot();
//        telegramCreatedBot.setBotUsername(telegramCreatedBotRequest.getBotUsername());
//        telegramCreatedBot.setBotToken(telegramCreatedBotRequest.getBotToken());
//        telegramCreatedBot.setBotLink(telegramCreatedBotRequest.getBotLink());
//        return telegramCreatedBotRepository.save(telegramCreatedBot);
//    }

//    @Override
//    public TelegramCreatedBot getBotByBotUsername(String botUsername) {
//        TelegramCreatedBot telegramCreatedBot = telegramCreatedBotRepository.getBotByBotUsername(botUsername);
//        return telegramCreatedBot;
//    }

//    @Override
//    public Optional<TelegramCreatedBot> getBotByBotId(Long botId) {
//        Optional<TelegramCreatedBot> telegramCreatedBot = telegramCreatedBotRepository.findById(botId);
//        return telegramCreatedBot;
//    }

//    @Override
//    public TelegramCreatedBot updateBotByBotUsername(String botUsername, TelegramCreatedBotRequest telegramCreatedBotRequest) {
//        TelegramCreatedBot bot = telegramCreatedBotRepository.getBotByBotUsername(botUsername);
//        bot.setBotUsername(telegramCreatedBotRequest.getBotUsername());
//        bot.setBotToken(telegramCreatedBotRequest.getBotToken());
//        bot.setBotLink(telegramCreatedBotRequest.getBotLink());
//        return telegramCreatedBotRepository.save(bot);
//    }


//    @Override
//    public void deleteBotById(Long botId) {
//        Optional<TelegramCreatedBot> botToDelete = telegramCreatedBotRepository.findById(botId);
//
//        if (botToDelete.isEmpty()) {
//            System.out.println("Bot with botId " + botId + " not found.");
//            throw new EntityNotFoundException("Bot not found with botId: " + botId);
//        }
//
//        List<Long> botIdAtIndexZero = telegramCreatedBotRepository.getBotIdAtIndexZero();
//        if (!botIdAtIndexZero.isEmpty() && botIdAtIndexZero.get(0).equals(botId)) {
//            System.out.println("Cannot delete bot with botId " + botId + " stored in index 0.");
//             throw new IllegalArgumentException("Cannot delete bot with botId " + botId + " stored in index 0.");
//        } else {
//            telegramCreatedBotRepository.deleteById(botId);
//        }
//    }


}
