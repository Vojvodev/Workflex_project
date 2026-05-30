package com.workflex.workation.service;

/** Raised when the workations CSV cannot be read or parsed. */
public class CsvImportException extends RuntimeException {

    public CsvImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvImportException(String message) {
        super(message);
    }
}
