package com.example.model.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "telegram_bots")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TelegramCreatedBot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String botUsername;
    private String botToken;
    private String botLink;

}
