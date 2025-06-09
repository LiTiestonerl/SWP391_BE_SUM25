package com.example.smoking_cessation_platform.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidation(MethodArgumentNotValidException exception){
        String message = "";
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()){
            message+= fieldError.getDefaultMessage();
        }
        return ResponseEntity.ok(message);
    }
}
