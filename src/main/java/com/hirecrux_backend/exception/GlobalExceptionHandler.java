package com.hirecrux_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request){

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setPath(request.getServletPath());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException exception, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError(HttpStatus.CONFLICT.getReasonPhrase());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setPath(request.getServletPath());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException exception, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        errorResponse.setPath(request.getServletPath());

        return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request){
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse();
        validationErrorResponse.setTimeStamp(LocalDateTime.now());
        validationErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        validationErrorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        Map<String, String> validationErrors = new HashMap<>();
        BindingResult bindingResult = exception.getBindingResult();
        for(FieldError fieldError: bindingResult.getFieldErrors()){
            validationErrors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
        validationErrorResponse.setErrors(validationErrors);
        validationErrorResponse.setPath(request.getServletPath());

        return new ResponseEntity<>(validationErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        errorResponse.setMessage("You do not have permission to access this resource");
        errorResponse.setPath(request.getServletPath());

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(
            InvalidOperationException exception,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setPath(request.getServletPath());

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.BAD_REQUEST
        );
    }
}
