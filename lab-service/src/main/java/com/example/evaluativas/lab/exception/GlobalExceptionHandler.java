package com.example.evaluativas.lab.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String K_STATUS = "status";
  private static final String K_TS = "timestamp";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
    Map<String, Object> body = new HashMap<>();
    body.put(K_STATUS, HttpStatus.BAD_REQUEST.value());
    body.put(K_TS, LocalDateTime.now());
    body.put("errors", errors);
    log.warn("Validación fallida: {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
    Map<String, Object> body = new HashMap<>();
    body.put(K_STATUS, HttpStatus.NOT_FOUND.value());
    body.put(K_TS, LocalDateTime.now());
    body.put("error", ex.getMessage());
    body.put("path", req.getDescription(false).replace("uri=", ""));
    log.error("No encontrado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex, WebRequest req) {
    Map<String, Object> body = new HashMap<>();
    body.put(K_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put(K_TS, LocalDateTime.now());
    body.put("error", "Error interno del servidor: " + ex.getMessage());
    body.put("path", req.getDescription(false).replace("uri=", ""));
    log.error("Error interno", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
