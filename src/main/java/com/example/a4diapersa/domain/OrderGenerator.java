package com.example.a4diapersa.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderGenerator {
    private static final Random random = new Random();

    public static Order generateOrder(List<Product> availableProducts) {
        int productCount = random.nextInt(5) + 1;

        ArrayList<Product> productsInOrder = new ArrayList<>();

        for (int i = 0; i < productCount; i++) {
            Product product = availableProducts.get(random.nextInt(availableProducts.size()));
            productsInOrder.add(product);
        }

        LocalDate deliveryDate = LocalDate.now().plusDays(random.nextInt(30) + 1);

        return new Order(productsInOrder, deliveryDate);
    }

    public static List<Order> generateOrders(List<Product> availableProducts, int count) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            orders.add(generateOrder(availableProducts));
        }
        return orders;
    }
}
