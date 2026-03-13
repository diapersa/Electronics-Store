package com.example.a4diapersa.repository;


import com.example.a4diapersa.domain.Entity;
import com.example.a4diapersa.exceptions.RepositoryException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class TextFileRepository<E extends Entity> extends MemoryRepository<E> {
    private final Path path;
    private final Factory<E> factory;

    public TextFileRepository(Path path, Factory<E> factory) {
        this.path = path;
        this.factory = factory;
        load();
    }

    private void load(){
        if(!Files.exists(path)) // first run: the file doesn't exist
            return;

        try {
            for(String line: Files.readAllLines(path)){
                if(line.isBlank())
                    continue;

                String[] tokens = line.split(";");
                super.add(factory.fromTokens(tokens));
            }
        }catch (IOException e){
            throw new RepositoryException("Error reading file " + path, e);
        }
    }

    private void persist(){
        try{
            if(path.getParent() != null)
                Files.createDirectories(path.getParent());

            var lines = new ArrayList<String>();

            for(E e: super.getAll())
                lines.add(factory.toLine(e));

            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        }catch (IOException e){
            throw new RepositoryException("Error writing file " + path, e);
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
