package com.warehub.warehub.common.response;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private boolean success = false;
    private T data;

    public ApiResponse(int statusCode, String statusDescription) {
        this.statusCode = statusCode;
        this.message = statusDescription;

        if (statusCode == HttpStatus.OK.value()) {
            success = true;
        }
    }

    public static <T> ResponseEntity<ApiResponse<T>> failedResponse(String message) {
        return failedResponse(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> failedResponse(T data) {
        return failedResponse(HttpStatus.BAD_REQUEST.value(), null, data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> failedResponse(int statusCode, String message) {
        return failedResponse(statusCode, message, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> failedResponse(int statusCode, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(statusCode, message);
        response.setSuccess(false);
        response.setData(data);
        return ResponseEntity.status(statusCode).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> successfulResponse(String message) {
        return successfulResponse(HttpStatus.OK.value(), message, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> successfulResponse(String message, T data) {
        return successfulResponse(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> successfulResponse(int statusCode, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(statusCode, message);
        response.setSuccess(true);
        response.setData(data);
        return ResponseEntity.status(statusCode).body(response);
    }
}
