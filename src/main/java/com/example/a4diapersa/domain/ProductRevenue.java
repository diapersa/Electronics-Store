package com.example.a4diapersa.domain;

public class ProductRevenue {
    private final Product product;
    private final int totalQuantity;
    private final double revenue;

    public ProductRevenue(Product product, int totalQuantity) {
        this.product = product;
        this.totalQuantity = totalQuantity;
        this.revenue = product.getProductPrice() * totalQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getRevenue() {
        return revenue;
    }

    @Override
    public String toString() {
        return product.getProductName() +
                " | Quantity: " + totalQuantity +
                " | Revenue: " + revenue + " lei";
    }
}
