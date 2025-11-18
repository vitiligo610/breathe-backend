package com.aqi.exception.handler;

import com.aqi.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request
    ) {

        Map<String, Object> body = Map.of(
            "timestamp", System.currentTimeMillis(),
            "status", HttpStatus.NOT_FOUND.value(),
            "error", "Not Found",
            "message", ex.getMessage(),
            "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}