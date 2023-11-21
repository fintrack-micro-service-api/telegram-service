package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(name = "telegram_users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private UUID userId;
    private Boolean isSubscribed;

    public TelegramUser(Long chatId, UUID uuid, boolean b) {
    }
}
