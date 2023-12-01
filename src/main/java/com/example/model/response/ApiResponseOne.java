package com.example.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseOne<T> {
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T payload;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LoginResponse loginResponse;

    private Integer status;

    private Long total;

    public ApiResponseOne(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public ApiResponseOne(String getAllHistoriesSuccessfully, int value, T content, long totalElements) {
    }
}
