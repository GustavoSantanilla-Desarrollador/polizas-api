package com.example.polizas.web;

import com.example.polizas.exception.BusinessException;
import com.example.polizas.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Map<String, Object>> notFound(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(404, ex.getMessage(), req));
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<Map<String, Object>> conflict(BusinessException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(409, ex.getMessage(), req));
    }

    /** Errores de @Valid en el body (campos faltantes/invalidos) -> 400, no 500. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(400, detalle, req));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> generic(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body(500, "Error interno", req));
    }

    private Map<String, Object> body(int status, String message, HttpServletRequest req) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "message", message,
                "path", req.getRequestURI()
        );
    }
}
