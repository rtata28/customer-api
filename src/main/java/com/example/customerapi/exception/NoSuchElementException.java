package com.example.customerapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchElementException extends RuntimeException {

    public NoSuchElementException(String message) {
        super(message);
    }
}

