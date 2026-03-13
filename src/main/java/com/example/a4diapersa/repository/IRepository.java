package com.example.a4diapersa.repository;


import com.example.a4diapersa.domain.Entity;
import com.example.a4diapersa.exceptions.RepositoryException;

import java.util.List;

public interface IRepository<E extends Entity> {
    void add(E entity) throws RepositoryException;

    void delete(E entity) throws RepositoryException;

    default void delete(int id) {}

    default void update(E entity, int id) {};

    void update(E entity) throws RepositoryException;

    E find(E entity) throws RepositoryException;

    boolean ifExists(E entity);

    List<E> getAll();

    E getById(int ID) throws RepositoryException;

    void clear();

    int getSize();
}
