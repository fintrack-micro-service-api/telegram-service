package com.example.repository;

import com.example.model.dto.TelegramCreatedBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramCreatedBotRepository extends JpaRepository<TelegramCreatedBot, Long> {

    TelegramCreatedBot getBotByBotUsername(String botName);

    @Query("SELECT t.id FROM TelegramCreatedBot t ORDER BY t.id ASC")
    List<Long> getBotIdAtIndexZero();

    Optional<TelegramCreatedBot> findByBotUsernameAndBotTokenAndBotLink(String botUsername, String botToken, String botLink);

}
