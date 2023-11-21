package com.example.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TelegramCreatedBotRequest {
    private String botUsername;
    private String botToken;
    private String botLink;
}
