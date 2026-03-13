package com.example.a4diapersa.repository;


import com.example.a4diapersa.domain.Entity;
import com.example.a4diapersa.exceptions.RepositoryException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class BinaryFileRepository<E extends Entity> extends MemoryRepository<E> {
    private final Path path;

    public BinaryFileRepository(Path path){
        this.path = path;
        load();
    }

    private void load(){
        if(!Files.exists(path))
            return;         // file will be created on the first run

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            var list = (ArrayList<E>) in.readObject();
            super.clear();

            for(E e: list)
                super.add(e);

        } catch (IOException e){
            throw new RepositoryException("Binary loading error from: " + path, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void persist(){
        try{
            if(path.getParent() != null)
                Files.createDirectories(path.getParent());

            try(ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))){
                out.writeObject(super.getAll());
            }
        }catch (IOException e){
            throw new RepositoryException("Error in binary saving in: " + path,e);
        }
    }

    @Override
    public void add(E entity) {
        super.add(entity);
        persist();
    }

    @Override
    public void delete(E entity) {
        super.delete(entity);
        persist();
    }

    @Override
    public void update(E entity) {
        super.update(entity);
        persist();
    }

    @Override
    public void clear() {
        super.clear();
        persist();
    }
}
