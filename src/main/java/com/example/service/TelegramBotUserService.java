package com.example.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.config.TelegramUserConfig;
import com.example.config.WebClientConfig;
import com.example.model.dto.BalanceDto;
import com.example.exception.BadRequestException;
import com.example.model.TelegramUser;
import com.example.model.dto.ScheduleDto;
import com.example.model.dto.TransactionHistoryDto;
import com.example.model.dto.UserDtoClient;
import com.example.model.response.ApiResponse;
import com.example.repository.TelegramUserRepository;
import com.vdurmont.emoji.EmojiParser;
import javassist.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
public class TelegramBotUserService extends TelegramLongPollingBot {

    private final TelegramUserConfig telegramUserConfig;
    private final WebClientConfig webClientConfig;
    private final TelegramUserRepository telegramUserRepository;

    public TelegramBotUserService(TelegramUserConfig telegramUserConfig, WebClientConfig webClientConfig, TelegramUserRepository telegramUserRepository) {
        this.telegramUserConfig = telegramUserConfig;
        this.webClientConfig = webClientConfig;
        this.telegramUserRepository = telegramUserRepository;

        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "Start interacting with the bot"));
        botCommandList.add(new BotCommand("/help", "Get information and assistance"));
        botCommandList.add(new BotCommand("/balance", "Check your account balance"));

        try {
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }


    @Override
    public String getBotUsername() {
        return telegramUserConfig.botUsername();
    }

    @Override
    public String getBotToken() {
        try {
        return telegramUserConfig.botToken();

        }catch (Exception e){
            throw new RuntimeException("Error message: " + e.getMessage());
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
       try {
           String emojiThanks = EmojiParser.parseToUnicode(":pray:");
           String emojiHeart = EmojiParser.parseToUnicode(":heart:");


//           **************************************************************************
           //codes in this block for popup url just a string when users click on button bind
           if (update.hasCallbackQuery()) {
               CallbackQuery callbackQuery = update.getCallbackQuery();
               String callbackData = callbackQuery.getData();

               // Check if the callback data starts with "bind_"
               if (callbackData.startsWith("bind_")) {
                   Long chatId = Long.valueOf(callbackData.substring("bind_".length()));
                   String url = "http://localhost:4200/home/?chatId=" + chatId;

                   SendMessage sendMessage = new SendMessage();
                   sendMessage.setChatId(chatId);
                   sendMessage.setText("Bind's link: " + url);

                   try {
                       execute(sendMessage);
                   } catch (TelegramApiException e) {
                       e.printStackTrace();
                   }
               }
           }
//           **************************************************************************


           if (update.hasMessage() && update.getMessage().hasText()) {
               Long chatId = update.getMessage().getChatId();
               String messageText = update.getMessage().getText().toLowerCase();

               if (messageText.equals("/start")) {
                   if (telegramUserRepository.findUserByChatId(chatId) == null) {
                       handleStartCommand(chatId);
                   } else {
                       prepareAndSendMessage(
                               chatId,
                               "This telegram account has bind with KB Prasac Bank's account already " + emojiHeart + "\n" +
                                       "Thank you for using our KB Prasac Bank Services " + emojiThanks
                       );
                   }
               } else if (messageText.equals("/help")) {
                   handleHelpCommand(chatId);
               } else if (messageText.equals("/balance")) {
                   if (telegramUserRepository.findUserByChatId(chatId) != null) {
                       handleBalanceCommand(chatId);
                   } else {
                       prepareAndSendMessage(
                               chatId,
                               "This telegram account not bind with KB Prasac Bank's account yet!"
                       );
                   }
               } else {
                   prepareAndSendMessage(chatId, "Sorry, command was not recognized!");
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public void clearWebhook() {
        try {
            DeleteWebhook deleteWebhook = new DeleteWebhook();
            execute(deleteWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Boolean isSubscribed(Long chatId, String token) {
        String emojiThanks = EmojiParser.parseToUnicode(":pray:");
        String emojiCongratulation = EmojiParser.parseToUnicode(":tada:");
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String userId = decodedJWT.getSubject();
            if (userId != null) {
                TelegramUser telegramUser = telegramUserRepository.findByChatId(chatId);
                if (telegramUser != null)
                    return false;
                else
                    prepareAndSendMessage(
                            chatId,
                            "Congratulations you have bind your account with KB Prasac Bank's account successfully " + emojiCongratulation + "\n" +
                            "Thank you for using our KB Prasac Bank Services " + emojiThanks
                    );
                Boolean saveInDatabase = telegramUserRepository.save(new TelegramUser(chatId, UUID.fromString(userId), true)).getIsSubscribed();

//                String saveTelegramNotificationByUserIdUrl = "http://client-event-service/api/v1/clients/save-telegram-notification-by-userId";
                String saveTelegramNotificationByUserIdUrl = "http://localhost:8088/api/v1/clients/save-telegram-notification-by-userId";
                WebClient saveTelegramNotificationByUserId = webClientConfig.webClientBuilder().baseUrl(saveTelegramNotificationByUserIdUrl).build();

                saveTelegramNotificationByUserId.post()
                        .uri("/{userId}", UUID.fromString(userId))
                        .retrieve()
                        .bodyToMono(BalanceDto.class)
                        .block();

                return saveInDatabase;
            }
            throw new NotFoundException("user id is not found");
        } catch (Exception e) {
            throw new BadRequestException("token can not null");
        }
    }

    public void sendUrlButtonMessage(Long chatId, String text) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton urlButton = new InlineKeyboardButton();
        urlButton.setText("Go bind");

//        can setUrl only https
//        urlButton.setUrl("http://localhost:4200/home/?chatId=" + chatId);

        //used for click bind button for popup angular url in localhost
        urlButton.setCallbackData("bind_" + chatId);

        keyboard.add(Collections.singletonList(urlButton));
        markup.setKeyboard(keyboard);

        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            message.setReplyMarkup(markup);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleStartCommand(Long chatId) {
        String notificationEmoji = EmojiParser.parseToUnicode(":bell:");
        String emojiHeart = EmojiParser.parseToUnicode(":heart:");
        String botEmoji = EmojiParser.parseToUnicode(":robot_face:");

        String answer = "Welcome to KB Prasac Bank's bot " + emojiHeart + "\n" +
                "Click the button below to bind your account and navigate to the application" +
                " for get notifications " + notificationEmoji + " of your transactions from this bot " + botEmoji;

        sendUrlButtonMessage(chatId, answer);
    }

    private void prepareAndSendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleHelpCommand(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        String helpMessage = "Welcome to KB Prasac Bank's bot! \n Here are some available commands:\n\n" +
                "/start - Start interacting with the bot\n" +
                "/help - Get information and assistance\n" +
                "/balance - Check your account balance\n\n" +
                "If you have any questions or need further assistance, feel free to ask.";

        sendMessage.setText(helpMessage);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleBalanceCommand(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        String emojiKey = EmojiParser.parseToUnicode(":key:");
        String emojiThanks = EmojiParser.parseToUnicode(":pray:");
        String emojiDollar = "\uD83D\uDCB5";

        TelegramUser telegramUser = telegramUserRepository.findUserByChatId(chatId);
        UUID userId = telegramUser.getUserId();

//        String getBankInfoByUserIdUrl = "http://client-event-service/api/v1/bank/bankInfo";
        String getBankInfoByUserIdUrl = "http://localhost:8088/api/v1/bank/bankInfo";
        WebClient getBankInfoByUserId = webClientConfig.webClientBuilder().baseUrl(getBankInfoByUserIdUrl).build();

        ParameterizedTypeReference<ApiResponse<BalanceDto>> typeReference = new ParameterizedTypeReference<ApiResponse<BalanceDto>>() {};
        ApiResponse<BalanceDto> balanceDto = getBankInfoByUserId.get()
                .uri("/{userId}", userId)
                .retrieve()
                .bodyToMono(typeReference)
                .block();

        System.out.println("object " + balanceDto);

        String userAccountNumber = balanceDto.getPayload().getBankAccountNumber();
        BigDecimal userBalance = balanceDto.getPayload().getCurrentBalance();

        System.out.println("userBalance " + userBalance);

        if (userBalance != null) {
            String balanceMessage = "Your current account number is " + userAccountNumber + " " + emojiKey + "\n" +
                    "And your balance is $" + userBalance + " " + emojiDollar + "\n\n" +
                    "Thank you for using our KB Prasac Bank Services " + emojiThanks;
            sendMessage.setText(balanceMessage);
        } else {
            sendMessage.setText("Unable to retrieve your balance at the moment. Please try again later.");
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(Long chatId, TransactionHistoryDto transactionHistoryDto) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        String emojiCheckmark = EmojiParser.parseToUnicode(":white_check_mark:");
        String emojiThanks = EmojiParser.parseToUnicode(":pray:");
        String emojiHeart = EmojiParser.parseToUnicode(":heart:");
        String emojiKey = EmojiParser.parseToUnicode(":key:");
        String emojiDollar = "\uD83D\uDCB5";

        String firstName = getFirstName(transactionHistoryDto);
        String lastName = getLastName(transactionHistoryDto);

        String messageToUser = buildTransactionMessage(transactionHistoryDto, firstName, lastName,
                emojiHeart, emojiThanks, emojiCheckmark, emojiKey, emojiDollar);

        sendMessage.setText(messageToUser);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    public void sendTextMessageSchedule(Long chatId, ScheduleDto scheduleDto) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        UUID customerId = UUID.fromString(scheduleDto.getUserId());
        transactionHistoryDto.setCustomerId(customerId);
        sendMessage.setText(scheduleDto.getMessage());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getFirstName(TransactionHistoryDto transactionHistoryDto) {
        UUID userId = transactionHistoryDto.getCustomerId();

        System.out.println("userId: " + transactionHistoryDto.getCustomerId());

//        String getCustomerByIdUrl = "http://client-event-service/api/v1/customers";
        String getCustomerByIdUrl = "http://localhost:8088/api/v1/customers";
        WebClient getCustomerById = webClientConfig.webClientBuilder().baseUrl(getCustomerByIdUrl).build();

        ParameterizedTypeReference<ApiResponse<UserDtoClient>> typeReference = new ParameterizedTypeReference<ApiResponse<UserDtoClient>>() {};
        ApiResponse<UserDtoClient> userDtoClient = getCustomerById.get()
                .uri("/{userId}", userId)
                .retrieve()
                .bodyToMono(typeReference)
                .block();

        System.out.println("object: " + userDtoClient);

        System.out.println("firstname: " + userDtoClient.getPayload().getFirstName());

        return userDtoClient.getPayload().getFirstName();
    }

    private String getLastName(TransactionHistoryDto transactionHistoryDto) {
        UUID userId = transactionHistoryDto.getCustomerId();
//        String getCustomerByIdUrl = "http://client-event-service/api/v1/customers";
        String getCustomerByIdUrl = "http://localhost:8088/api/v1/customers";
        WebClient getCustomerById = webClientConfig.webClientBuilder().baseUrl(getCustomerByIdUrl).build();

        ParameterizedTypeReference<ApiResponse<UserDtoClient>> typeReference = new ParameterizedTypeReference<ApiResponse<UserDtoClient>>() {};
        ApiResponse<UserDtoClient> userDtoClient = getCustomerById.get()
                .uri("/{userId}", userId)
                .retrieve()
                .bodyToMono(typeReference)
                .block();

        return userDtoClient.getPayload().getLastName();
    }

    private String buildTransactionMessage(TransactionHistoryDto transactionHistoryDto, String firstName, String lastName,
                                           String emojiHeart, String emojiThanks, String emojiCheckmark, String emojiKey, String emojiDollar) {
        BigDecimal amount = transactionHistoryDto.getAmount();
        String fromAccountNumber = transactionHistoryDto.getBankAccountNumber();
        String receiverAccountNumber = transactionHistoryDto.getReceivedAccountNumber();
        String senderAccountNumber = transactionHistoryDto.getBankAccountNumber();

        return switch (transactionHistoryDto.getType()) {
            case WITHDRAW ->
                    buildWithdrawMessage(firstName, lastName, emojiHeart, emojiThanks, emojiCheckmark, emojiKey, emojiDollar, amount, fromAccountNumber);
            case SENDER ->
                    buildSenderMessage(firstName, lastName, emojiHeart, emojiThanks, emojiCheckmark, emojiKey, emojiDollar, amount, senderAccountNumber, receiverAccountNumber);
            case RECEIVER ->
                    buildReceiverMessage(firstName, lastName, emojiHeart, emojiThanks, emojiCheckmark, emojiKey, emojiDollar, amount, senderAccountNumber, receiverAccountNumber);
            case DEPOSIT ->
                    buildDepositMessage(firstName, lastName, emojiHeart, emojiThanks, emojiCheckmark, emojiKey, emojiDollar, amount, fromAccountNumber);
            default -> "";
        };
    }

    private String buildWithdrawMessage(String firstName, String lastName, String emojiHeart, String emojiThanks, String emojiCheckmark, String emojiKey, String emojiDollar,
                                        BigDecimal amount, String fromAccountNumber) {
        return "Hello " + firstName + " " + lastName + " " + emojiHeart + "\n\n" +
                "You have WITHDRAWN $" + amount + " " + emojiDollar + "\n" +
                "From this account number " + fromAccountNumber + " " + emojiKey + "\n\n" +
                "We confirm your transaction has completed " + emojiCheckmark + "\n" +
                "Thank you for using our KB Prasac Bank Services " + emojiThanks;
    }

    private String buildSenderMessage(String firstName, String lastName, String emojiHeart, String emojiThanks, String emojiCheckmark, String emojiKey, String emojiDollar,
                                      BigDecimal amount, String senderAccountNumber, String receiverAccountNumber) {
        return "Hello " + firstName + " " + lastName + " " + emojiHeart + "\n\n" +
                "You have TRANSFERRED $" + amount + " " + emojiDollar + "\n" +
                "From this account number " + senderAccountNumber + " " + emojiKey + "\n" +
                "To this account number " + receiverAccountNumber + " " + emojiKey + "\n\n" +
                "We confirm your transaction has completed " + emojiCheckmark + "\n" +
                "Thank you for using our KB Prasac Bank Services " + emojiThanks;
    }

    private String buildReceiverMessage(String firstName, String lastName, String emojiHeart, String emojiThanks, String emojiCheckmark, String emojiKey, String emojiDollar,
                                        BigDecimal amount, String senderAccountNumber, String receiverAccountNumber) {
        return "Hello " + firstName + " " + lastName + " " + emojiHeart + "\n\n" +
                "You have RECEIVED $" + amount + " " + emojiDollar + "\n" +
                "From this account number " + receiverAccountNumber + " " + emojiKey + "\n" +
                "This is your account number that received " + senderAccountNumber + " " + emojiKey + "\n\n" +
                "We confirm your transaction has completed " + emojiCheckmark + "\n" +
                "Thank you for using our KB Prasac Bank Services " + emojiThanks;
    }

    private String buildDepositMessage(String firstName, String lastName, String emojiHeart, String emojiThanks, String emojiCheckmark, String emojiKey, String emojiDollar,
                                       BigDecimal amount, String fromAccountNumber) {
        return "Hello " + firstName + " " + lastName + " " + emojiHeart + "\n\n" +
                "You have DEPOSITED $" + amount + " " + emojiDollar + "\n" +
                "To this account number " + fromAccountNumber + " " + emojiKey +
                "\n\nWe confirm your transaction has completed " + emojiCheckmark +
                "\nThank you for using our KB Prasac Bank Services " + emojiThanks;
    }


}
