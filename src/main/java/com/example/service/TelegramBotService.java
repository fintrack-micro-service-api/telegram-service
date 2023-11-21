//package com.example.service;
//
//
//import com.auth0.jwt.JWT;
//import com.example.config.TelegramBotConfig;
//import com.example.exception.BadRequestException;
//import com.example.exception.NotFoundException;
//import com.example.model.Telegram;
//import com.example.repository.TelegramRepository;
//import com.example.response.ApiResponse;
//import com.vdurmont.emoji.EmojiParser;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import com.auth0.jwt.interfaces.DecodedJWT;
//
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//
//@Service
//public class TelegramBotService extends TelegramLongPollingBot {
//
//    private final TelegramBotConfig telegramBotConfig;
//    private final TelegramRepository telegramRepository;
//
//    @Value("${fontEnd.Url}")
//    private String fontEndUrl;
//
//    public TelegramBotService(TelegramBotConfig telegramBotConfig, TelegramRepository userRepository) {
//        this.telegramBotConfig = telegramBotConfig;
//        this.telegramRepository = userRepository;
//    }
//
//    @Override
//    public String getBotUsername() {
//        return telegramBotConfig.getBotName();
//    }
//
//    @Override
//    public String getBotToken() {
//        return telegramBotConfig.getToken();
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String messageText = update.getMessage().getText();
//            Long chatId = update.getMessage().getChatId();
//
//            if (messageText.equals("/start")) {
//                if (telegramRepository.findUserByChatId(chatId) == null) {
//                    startCommandReceived(chatId, update.getMessage().getChat().getLastName(), update.getMessage().getChat().getFirstName());
//                }
//            } else {
//                prepareAndSendMessage(chatId, "Sorry, command was not recognized!");
//            }
//        }
//
//    }
//
//
//    public boolean isSubscribe(String chatId, String token) {
//        try {
//            DecodedJWT decodedJWT = JWT.decode(token);
//            String userId = decodedJWT.getSubject();
//            if (userId != null) {
//                Telegram telegram = telegramRepository.findByChatId(Long.valueOf(chatId));
//                if (telegram != null)
//                    return false;
//                else
//                    prepareAndSendMessage(Long.valueOf(chatId), "សូមអបអរសាទរអ្នកបានភ្ជាប់គណនី FINTRACK ដោយជោគជ័យ។");
//                return telegramRepository.save(new Telegram(Long.valueOf(chatId), UUID.fromString(userId), true)).getIsSubscribe();
//            }
//            throw new NotFoundException("user id is not found");
//        } catch (Exception e) {
//            throw new BadRequestException("token can not null");
//        }
//    }
//
//
//    public void sendUrlButtonMessage(Long chatId, String text) {
//        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//
//        InlineKeyboardButton urlButton = new InlineKeyboardButton();
//        urlButton.setText("Go bind account");
//
////        urlButton.setUrl(fontEndUrl+"/application/4/dashboard?chat_id="+chatId); // Set your URL here
//        urlButton.setUrl("https://google.com" + chatId); // Set your URL here
//
//        keyboard.add(Collections.singletonList(urlButton));
//        markup.setKeyboard(keyboard);
//
//        try {
//            SendMessage message = new SendMessage();
//            message.setChatId(chatId);
//            message.setText(text);
//            message.setReplyMarkup(markup);
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void startCommandReceived(Long chatId, String firstName, String lastname) {
//        String answer = EmojiParser.parseToUnicode("Hello, " + lastname + " " + firstName + "\n" +
//                        "FINTRACK notification message send to telegram in real time, please bind FINTRACK account first! \n"
//                + fontEndUrl+"/application/4/dashboard?chat_id=" + chatId
//        );
////        sendUrlButtonMessage(chatId, answer);
//        sendMessage(chatId, answer);
//    }
//
//
//    public void sendMessage(Long chatId, String textToSend) {
//
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(textToSend);
//        try {
//            execute(sendMessage);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public ApiResponse<?> sendMessage(String message, String token) {
////        if (principal.getName() == null) {
////            throw new NotFoundException("use need to login");
////        }
//        Telegram telegram = telegramRepository.findByUserId(UUID.fromString("ec79a214-f589-4349-8a59-b0a10c541555"));
//        if (telegram != null) {
//            String answer = EmojiParser.parseToUnicode(message);
//            sendMessage(telegram.getChatId(), answer);
//        }
//        return ApiResponse.builder()
//                .message("send message success")
//                .status(200)
//                .build();
//    }
//
//    private void prepareAndSendMessage(Long chatId, String text) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(chatId);
//        sendMessage.setText(text);
//        try {
//            execute(sendMessage);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
