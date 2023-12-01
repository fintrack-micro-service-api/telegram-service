package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "telegram_histories")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TelegramHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageName;
    private String type;
    private String status;
    private LocalDateTime date;

}
