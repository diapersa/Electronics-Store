package com.example.a4diapersa.service;

import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.exceptions.ValidationException;
import com.example.a4diapersa.repository.IRepository;

import java.util.List;

public class ProductService {
//    private final MemoryRepository<Product>  repository;
    private final IRepository<Product> repository;

    public ProductService(IRepository<Product> productRepo){
        this.repository = productRepo;
    }

    public void addProduct(Product product) {
        if(product.getProductPrice() < 0)
            throw new ValidationException("Product price cannot be negative");

        repository.add(product);
    }

    public Product getProduct(Product product) {
        return repository.find(product);
    }

    public void deleteProduct(Product product) {
        repository.delete(product);
    }

    public void deleteById(int id) {
        repository.delete(id);
    }

    public void updateProduct(Product product) {
        repository.update(product);
    }

    public List<Product> getAllProducts(){
        return repository.getAll();
    }

    public Product getByID(int ID) {
        return repository.getById(ID);
    }

    public boolean ifExists(int id){
        List<Product> products = getAllProducts();

        for(Product p : products){
            if(p.getID() == id){
                return true;
            }
        }
        return false;
    }

    public int getSize(){
        return repository.getSize();
    }

}
