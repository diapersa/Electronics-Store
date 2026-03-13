package com.example.a4diapersa.domain;

import java.io.Serial;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class Order extends Entity{
    @Serial
    private static final long serialVersionUID = 1L;

    private ArrayList<Product> products;
    private LocalDate deliveryDate;

    public Order() {
        super(0);
    }

    public Order(int ID, ArrayList<Product> products, LocalDate deliveryDate){
        super(ID);
        this.products = products;
        this.deliveryDate = deliveryDate;
    }

    public Order(ArrayList<Product> products, LocalDate deliveryDate){
        super(0);
        this.products = products;
        this.deliveryDate = deliveryDate;
    }

    public int getOrderID(){
        return getID();
    }

    public ArrayList<Product> getProducts(){
        return this.products;
    }
    public void setProducts(ArrayList<Product> products){
        this.products = products;
    }

    public LocalDate getDeliveryDate(){
        return this.deliveryDate;
    }
    public void setDeliveryDate(LocalDate deliveryDate){
        this.deliveryDate = deliveryDate;
    }

    @Override
    public String toString() {
        return "ID=" + getID() + ", products=" + products + ", deliveryDate=" + deliveryDate;
    }
}
