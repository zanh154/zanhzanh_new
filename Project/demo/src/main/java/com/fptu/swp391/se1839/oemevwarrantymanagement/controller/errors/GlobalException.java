package com.fptu.swp391.se1839.oemevwarrantymanagement.controller.errors;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NoSuchElementException ex) {
        var result = ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .message("RESOURCE_NOT_FOUND")
                .errorCode(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        var result = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message("ILLEGAL_ARGUMENT")
                .errorCode(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        String errors = String.join("; ", errorList);
        var result = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message("VALIDATION_ERROR")
                .errorCode(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllException(Exception ex) {
        var result = ApiResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .message("INTERNAL_SERVER_ERROR")
                .errorCode(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
