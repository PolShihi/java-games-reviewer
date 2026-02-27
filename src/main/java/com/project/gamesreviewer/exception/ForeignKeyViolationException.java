package com.project.gamesreviewer.exception;

public class ForeignKeyViolationException extends RuntimeException {
    
    public ForeignKeyViolationException(String message) {
        super(message);
    }
    
    public ForeignKeyViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
