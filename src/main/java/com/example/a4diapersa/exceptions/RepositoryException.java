package com.example.a4diapersa.exceptions;

public class RepositoryException extends RuntimeException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(Throwable cause){
        super(cause);
    }

    public RepositoryException(String s, Throwable cause) {
        super(s,cause);
    }
}
