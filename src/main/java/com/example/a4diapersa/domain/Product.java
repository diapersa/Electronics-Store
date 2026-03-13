package com.example.a4diapersa.domain;

import java.io.Serial;
import java.util.Objects;

public class Product extends Entity{
    @Serial
    private static final long serialVersionUID = 1L;

    private String category;
    private String name;
    private int price;

    public Product(int ID, String category, String name, int price){
        super(ID);
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public Product(String category, String name, int price){
        super(0);
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public int getProductID(){
        return getID();
    }

    public String getCategory(){
        return this.category;
    }
    public void setCategory(String category){
        this.category = category;
    }

    public String getProductName(){
        return this.name;
    }
    public void setProductName(String name){
        this.name = name;
    }

    public int getProductPrice(){
        return this.price;
    }
    public void setProductPrice(int price){
        this.price = price;
    }

    @Override
    public String toString() {
        return "ID=" + getID() + ", category=" + category + ", name=" + name + ", price=" + price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getID() == product.getID() && price == product.price && Objects.equals(category, product.category) && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), category, name, price);
    }
}
