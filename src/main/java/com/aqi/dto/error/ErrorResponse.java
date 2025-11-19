package com.aqi.dto.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private Long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
