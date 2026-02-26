package com.project.gamesreviewer.exception;

public class DuplicateEntryException extends RuntimeException {
    
    public DuplicateEntryException(String message) {
        super(message);
    }
    
    public DuplicateEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
