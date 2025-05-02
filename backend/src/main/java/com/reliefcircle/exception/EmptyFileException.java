package com.reliefcircle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyFileException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public EmptyFileException(String message) {
        super(message);
    }
    
    public EmptyFileException(String message, Throwable cause) {
        super(message, cause);
    }
}