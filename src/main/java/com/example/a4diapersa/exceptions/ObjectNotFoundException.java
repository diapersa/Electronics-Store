package com.example.a4diapersa.exceptions;

public class ObjectNotFoundException extends RepositoryException {
    public ObjectNotFoundException(int id) {
        super("There is no object with id " + id);
    }
}
