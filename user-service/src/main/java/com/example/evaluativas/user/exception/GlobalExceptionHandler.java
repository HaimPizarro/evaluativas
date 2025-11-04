package com.example.evaluativas.user.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Claves JSON repetidas
    private static final String KEY_STATUS = "status";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_ERROR = "error";
    private static final String KEY_ERRORS = "errores";
    private static final String KEY_PATH = "path";

    // 400 - Validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));

        log.warn("Validación fallida: {}", errores);

        Map<String, Object> body = new HashMap<>();
        body.put(KEY_STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(KEY_TIMESTAMP, LocalDateTime.now());
        body.put(KEY_ERRORS, errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 404 - No encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex, WebRequest req) {
        log.error("Recurso no encontrado: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put(KEY_STATUS, HttpStatus.NOT_FOUND.value());
        body.put(KEY_TIMESTAMP, LocalDateTime.now());
        body.put(KEY_ERROR, ex.getMessage());
        body.put(KEY_PATH, req.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 500 - General
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex, WebRequest req) {
        log.error("Error interno: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put(KEY_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put(KEY_TIMESTAMP, LocalDateTime.now());
        body.put(KEY_ERROR, "Error interno del servidor: " + ex.getMessage());
        body.put(KEY_PATH, req.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
