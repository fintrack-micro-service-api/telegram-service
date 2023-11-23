package com.example.controller;

import com.example.service.TelegramBotUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/telegram/users")
@AllArgsConstructor
@SecurityRequirement(name = "auth")
public class TelegramUserController {

    private final TelegramBotUserService telegramBotUserService;


    @GetMapping("/subscribeddddddddddddddddseyha")
    public Boolean subscribe(@RequestParam Long chat_id, @RequestParam String token){
        return telegramBotUserService.isSubscribed(chat_id, token);
    }



}
