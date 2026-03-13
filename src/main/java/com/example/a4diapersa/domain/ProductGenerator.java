package com.example.a4diapersa.domain;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProductGenerator {
    private static final Random random = new Random();

    private static final List<String> categories = List.of(
            "Laptop", "Smartphone", "Tablet", "TV", "Monitor", "Headphones"
    );

    private static final Map<String, List<String>> brands = Map.of(
      "Laptop", List.of("Asus", "Dell", "HP", "Lenovo", "MacBook"),
            "Smartphone", List.of("Samsung", "Apple", "Huawei", "OnePlus"),
            "Tablet", List.of("Apple", "Samsung", "Lenovo"),
            "TV", List.of("LG", "Samsung", "Sony"),
            "Monitor", List.of("Dell", "Asus", "LG"),
            "Headphones", List.of("JBL", "Sony", "Samsung")
    );

    public static Product generateProduct(){
        String category = categories.get(random.nextInt(categories.size()));

        List<String> names = brands.get(category);
        String name = names.get(random.nextInt(names.size()));

        int price;

        switch (category){
            case "Laptop" -> price = random.nextInt(4000) + 1000;
            case "Smartphone" -> price = random.nextInt(3000) + 500;
            case "Tablet" -> price = random.nextInt(2000) + 400;
            case "TV" -> price = random.nextInt(5000) + 1500;
            case "Monitor" -> price = random.nextInt(1500) + 500;
            default -> price = random.nextInt(1000) + 100;
        }
        return new Product(category, name, price);
    }
}
