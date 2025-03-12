package com.rgalim.gptbot.controller;

import com.rgalim.gptbot.exception.TokenValidationException;
import com.rgalim.gptbot.model.telegram.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(TokenValidationException.class)
    private ResponseEntity<ErrorResponse> handleTokenValidationException(TokenValidationException ex) {
        log.error("Failed to validate token: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("The request is unauthorized"));
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Something went wrong"));
    }
}
