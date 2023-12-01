package com.example.service.implementation;

import com.example.model.TelegramHistory;
import com.example.model.response.ApiResponse;
import com.example.repository.TelegramHistoryRepository;
import com.example.service.TelegramHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TelegramHistoryServiceImp implements TelegramHistoryService {

    private final TelegramHistoryRepository telegramHistoryRepository;

    public TelegramHistoryServiceImp(TelegramHistoryRepository telegramHistoryRepository) {
        this.telegramHistoryRepository = telegramHistoryRepository;
    }

    @Override
    public Page<TelegramHistory> getAllHistories(int page, int size) {
        Pageable pageable = PageRequest.of (page, size);
        return telegramHistoryRepository.findAll(pageable);
    }

    @Override
    public ApiResponse<Object> deleteById(Long historyId) {
        Optional<TelegramHistory> optionalTelegramHistory = telegramHistoryRepository.findById(historyId);

        if (optionalTelegramHistory.isPresent()) {
            telegramHistoryRepository.deleteById(historyId);
            return ApiResponse.builder()
                    .message("Telegram history deleted successfully")
                    .status(HttpStatus.OK.value())
                    .build();
        } else {
            return ApiResponse.builder()
                    .message("Telegram history not found for ID: " + historyId)
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();
        }
    }

}
