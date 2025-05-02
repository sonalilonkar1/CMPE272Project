package com.reliefcircle.exception;

public class CharityException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;


	public CharityException(String message) {
        super(message);
    }
    
    public CharityException(String message, Throwable cause) {
        super(message, cause);
    }
}