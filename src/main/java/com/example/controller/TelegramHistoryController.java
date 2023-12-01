package com.example.controller;

import com.example.model.TelegramHistory;
import com.example.model.response.ApiResponse;
import com.example.model.response.ApiResponseOne;
import com.example.service.TelegramHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/telegram/histories")
@AllArgsConstructor
@CrossOrigin
public class TelegramHistoryController {

    private final TelegramHistoryService telegramHistoryService;

    @GetMapping("/get-all-histories")
    @Operation(summary = "Get all Telegram histories")
    public ApiResponseOne<?> getAllHistories(@RequestParam(name = "page", defaultValue = "0") int page,
                                             @RequestParam(name = "size", defaultValue = "5") int size) {
        Page<TelegramHistory> response = telegramHistoryService.getAllHistories(page, size);

        return ApiResponseOne.builder()
                .message("get Email Receiver Successfully")
                .payload(response.getContent())
                .total(response.getTotalElements())
                .status(200)
                .build();
    }

    @DeleteMapping("/delete/{historyId}")
    @Operation(summary = "Delete Telegram history by ID (Long)")
    public ResponseEntity<?> deleteHistoryById(@PathVariable Long historyId) {
        ApiResponse<Object> response = telegramHistoryService.deleteById(historyId);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
