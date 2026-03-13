package com.example.a4diapersa.repository;


import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.exceptions.RepositoryException;

import java.util.Arrays;

public class ProductFactory implements Factory<Product> {
    private static final String DELIM = ";";   // delimiter

    @Override
    public Product fromTokens(String[] tokens) {
        try{
            // format:  id;category;name;price

            if(tokens == null || tokens.length < 4){
                throw new RepositoryException("Invalid line for Product: " + Arrays.toString(tokens));
            }

            int id = Integer.parseInt(tokens[0].trim());
            String category = tokens[1].trim();
            String name = tokens[2].trim();
            int price = Integer.parseInt(tokens[3].trim());

            return new Product(id, category, name, price);
        }catch (NumberFormatException e){
            throw new RepositoryException("Invalid numeric format in line: " + Arrays.toString(tokens), e);
        }
    }

    @Override
    public String toLine(Product product) {
        // format: id;category;name;price
        return product.getID() + DELIM
                + safe(product.getCategory()) + DELIM
                + product.getProductName()+ DELIM
                + safe(String.valueOf(product.getProductPrice()));
    }

    private String safe(String s){
        return s == null ? "" : s.replace("\n", " ").trim();
    }
}
