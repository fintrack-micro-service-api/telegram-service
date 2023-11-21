package com.example.controller;

import com.example.model.dto.TelegramCreatedBot;
import com.example.model.response.ApiResponse;
import com.example.request.TelegramCreatedBotRequest;
import com.example.service.TelegramCreatedBotService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/telegram/bots")
@AllArgsConstructor
public class TelegramCreatedBotController {

    private final TelegramCreatedBotService telegramCreatedBotService;

    @GetMapping("/get-bots-from-database")
    @Operation(summary = "we used only bot index 0 in database to send notifications")
    public ResponseEntity<?> getBots() {
        List<TelegramCreatedBot> telegramCreatedBot = telegramCreatedBotService.getBots();
        ApiResponse<List<TelegramCreatedBot>> response = ApiResponse.<List<TelegramCreatedBot>>builder()
                .message("get bots successfully")
                .status(HttpStatus.OK.value())
                .payload(telegramCreatedBot)
                .build();
        return ResponseEntity.ok().body(response);
    }


    //this endpoint is update only bot that's store in index 0 in table
    @PutMapping("/update-bot")
    @Operation(summary = "you have to created bot, botUsername, botToken and botLink (required and cannot wrong) by ur own in your Telegram app first before updated it to use your own bot")
    public ResponseEntity<?> updateBot(@RequestBody TelegramCreatedBotRequest telegramCreatedBotRequest) {
        try {
            TelegramCreatedBot telegramCreatedBot = telegramCreatedBotService.updateBot(telegramCreatedBotRequest);
            ApiResponse<TelegramCreatedBot> response = ApiResponse.<TelegramCreatedBot>builder()
                    .message("Bot updated successfully")
                    .status(HttpStatus.OK.value())
                    .payload(telegramCreatedBot)
                    .build();
            return ResponseEntity.ok().body(response);
        } catch (EntityNotFoundException e) {
            ApiResponse<TelegramCreatedBot> errorResponse = ApiResponse.<TelegramCreatedBot>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalArgumentException e) {
            ApiResponse<TelegramCreatedBot> errorResponse = ApiResponse.<TelegramCreatedBot>builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ApiResponse<TelegramCreatedBot> errorResponse = ApiResponse.<TelegramCreatedBot>builder()
                    .message("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    //    @PostMapping("/create-bot")
//    @Operation(summary = "you have to created bot, botUsername or botToken(required and cannot wrong) by ur own in your Telegram app first before created it")
//    public ResponseEntity<?> createBot(@RequestBody TelegramCreatedBotRequest telegramCreatedBotRequest) {
//        TelegramCreatedBot telegramCreatedBot = telegramCreatedBotService.createBot(telegramCreatedBotRequest);
//        return ResponseEntity.ok().body(telegramCreatedBot);
//    }


//    @DeleteMapping("/delete-bot-by-botId/{botId}")
//    public ResponseEntity<?> deleteBotByBotId(@PathVariable Long botId) {
//        try {
//            telegramCreatedBotService.deleteBotById(botId);
//            return ResponseEntity.ok().body("Bot with botId " + botId + " deleted successfully.");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This botId is store in index 0, " + e.getMessage());
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
//        }
//    }

//    @GetMapping("/get-bot-by-botUsername")
//    @Operation(summary = "we used only bot index = 0 in database to send notifications")
//    public ResponseEntity<?> getBotByBotUsername(@RequestParam String botUsername) {
//        TelegramCreatedBot telegramCreatedBot = telegramCreatedBotService.getBotByBotUsername(botUsername);
//        return ResponseEntity.ok().body(telegramCreatedBot);
//    }
//
//    @GetMapping("/get-bot-by-botId/{botId}")
//    @Operation(summary = "we used only bot index = 0 in database to send notifications")
//    public ResponseEntity<?> getBotByBotId(@PathVariable Long botId) {
//        Optional<TelegramCreatedBot> telegramCreatedBot = telegramCreatedBotService.getBotByBotId(botId);
//        return ResponseEntity.ok().body(telegramCreatedBot);
//    }

//    @PutMapping("/update-bot-by-botUsername")
//    @Operation(summary = "you have to updated bot, botUsername or botToken(required and cannot wrong) by ur own in your Telegram app first before updated it")
//    public ResponseEntity<?> updateBotByBotUsername(@RequestParam String botUsername, @RequestBody TelegramCreatedBotRequest telegramCreatedBotRequest) {
//        try {
//            TelegramCreatedBot telegramCreatedBot = telegramCreatedBotService.updateBotByBotUsername(botUsername, telegramCreatedBotRequest);
//            return ResponseEntity.ok().body("Bot with botUsername " + botUsername + " updated successfully.");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bot not found");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
//        }
//    }


}
