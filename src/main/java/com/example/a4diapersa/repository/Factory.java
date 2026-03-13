package com.example.a4diapersa.repository;

public interface Factory<T> {
    T fromTokens(String[] tokens); // line -> obj
    String toLine(T entity);        // obj -> line
}
