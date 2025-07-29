package com.example.moonshot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MoonshotException extends RuntimeException {

    private final HttpStatus status;

    public MoonshotException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
