package com.example.a4diapersa.repository;


import com.example.a4diapersa.domain.Entity;
import com.example.a4diapersa.exceptions.DuplicateIdException;
import com.example.a4diapersa.exceptions.ObjectNotFoundException;
import com.example.a4diapersa.exceptions.RepositoryException;
import com.example.a4diapersa.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class MemoryRepository<E extends Entity> implements IRepository<E>{
    protected List<E> entities = new ArrayList<>();

    @Override
    public void add(E entity) {
        if(entity == null)
            throw new RepositoryException("Entity is null");

        if(entity.getID() < 0)
            throw new ValidationException("ID is negative");

        for(E e: entities)
            if(e.getID() == entity.getID())
                throw new DuplicateIdException(e.getID());

        entities.add(entity);
    }

    @Override
    public boolean ifExists(E entity) {
        for (E e:entities)
            if(e.getID() == entity.getID())
                return true;

        return false;
    }

    @Override
    public void delete(E entity) {
        if(ifExists(entity)){
            entities.remove(entity);
        }
        else
            throw new ObjectNotFoundException(entity.getID());
    }

    @Override
    public void delete(int id) {
        boolean removed = false;

        for(E e: entities){
            if(e.getID() == id){
                entities.remove(e);
                removed = true;
            }
        }

        if (!removed)
            throw new ObjectNotFoundException(id);
    }

    @Override
    public void update(E entity) {
        int updated = 0;

        for(int i = 0;  i < entities.size() && updated == 0; i++){
            E e = entities.get(i);
            if(e.getID() == entity.getID()){
                entities.set(i, entity);
                updated = 1;
            }
        }

        if(updated == 0)
            throw new ObjectNotFoundException(entity.getID());
    }

    @Override
    public E find(E entity) {
        for(E e: entities)
            if(e.getID() == entity.getID())
                return e;

        throw new ObjectNotFoundException(entity.getID());
    }

    @Override
    public List<E> getAll() {
        return new ArrayList<>(entities);
    }

    @Override
    public E getById(int ID) {
        for(E e: entities)
            if(e.getID() == ID)
                return e;

        throw new ObjectNotFoundException(ID);
    }

    @Override
    public void clear() {
        entities.clear();
    }

    @Override
    public int getSize() {
        return entities.size();
    }
}
