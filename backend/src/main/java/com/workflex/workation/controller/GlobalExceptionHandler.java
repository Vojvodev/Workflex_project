package com.workflex.workation.controller;

import com.workflex.workation.service.CsvImportException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CsvImportException.class)
    public ResponseEntity<Map<String, Object>> handleCsvImport(CsvImportException ex) {
        log.error("CSV import failed", ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "CSV import failed", "message", ex.getMessage()));
    }
}
