package com.example.a4diapersa.exceptions;

public class DuplicateIdException extends RepositoryException {
    public DuplicateIdException(int id) {
        super("There is already an object with id " + id);
    }
}
