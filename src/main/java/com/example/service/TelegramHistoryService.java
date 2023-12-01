package com.example.service;


import com.example.model.TelegramHistory;
import com.example.model.response.ApiResponse;
import org.springframework.data.domain.Page;

public interface TelegramHistoryService {
    Page<TelegramHistory> getAllHistories(int page, int size);

    ApiResponse<Object> deleteById(Long historyId);
}
