package com.hirecrux_backend.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ValidationErrorResponse {
    private LocalDateTime timeStamp;
    private Integer status;
    private String error;
    private Map<String, String> errors;
    private String path;
}
