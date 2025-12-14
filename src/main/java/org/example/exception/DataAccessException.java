package org.example.exception;

/**
 * Custom exception to indicate data access errors.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}