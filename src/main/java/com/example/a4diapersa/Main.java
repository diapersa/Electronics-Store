package com.example.a4diapersa;

import com.example.a4diapersa.domain.Order;
import com.example.a4diapersa.domain.OrderGenerator;
import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.domain.ProductGenerator;
import com.example.a4diapersa.exceptions.RepositoryException;
import com.example.a4diapersa.repository.*;
import com.example.a4diapersa.service.OrderService;
import com.example.a4diapersa.service.ProductService;
import com.example.a4diapersa.ui.ConsoleUI;
import javafx.application.Application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, RepositoryException {
        Properties prop = new Properties();

//        try (var in = Main.class.getResourceAsStream("/com/example/a4diapersa/settings.properties")) {
//            if (in != null) {
//                prop.load(in);
//            } else {
//                System.out.println("settings.properties not found in classpath!");
//            }
//        } catch (Exception e) {
//            System.out.println("Error reading settings.properties: " + e.getMessage());
//        }

        try{
            Path cfg = Path.of("src/main/resources/settings.properties");

            if(Files.exists(cfg)){
                try(var in = Files.newInputStream(cfg)){
                    prop.load(in);
                }
            }
        } catch (Exception e){
            System.out.println("Error reading settings.properties" + e.getMessage());
            // continuam cu valori din memorie
        }

        String repoType = prop.getProperty("Repository", "sql").toLowerCase();
        System.out.println("Repository type: " + repoType);

        Path productsTxt = Path.of(prop.getProperty("Products", "data/products.txt"));
        Path ordersTxt = Path.of(prop.getProperty("Orders", "data/orders.txt"));

        Path productsBin = Path.of(prop.getProperty("Products", "data/products.bin"));
        Path ordersBin = Path.of(prop.getProperty("Orders", "data/orders.bin"));

        // construim repo-urile pe baza setarilor

        MemoryRepository<Product> productRepo = switch (repoType) {
            case "text" -> new TextFileRepository<>(productsTxt, new ProductFactory());
            case "binary" -> new BinaryFileRepository<>(productsBin);
            case "sql" -> new SQLProductRepository();

            default -> throw new IllegalArgumentException("Unknown Repository type: " + repoType);
        };

        MemoryRepository<Order> orderRepo = switch (repoType){
            case "text" -> new TextFileRepository<>(ordersTxt, new OrderFactory());
            case "binary" -> new BinaryFileRepository<>(ordersBin);
            case "sql" -> new SQLOrderRepository(productRepo);

            default -> throw new IllegalArgumentException("Unknown Repository type: " + repoType);
        };

        // Service depinde de interfata, nu de implementare

        ProductService productService = new ProductService(productRepo);
        OrderService orderService = new OrderService(orderRepo);

        try {
            if (productService.getSize() == 0){
                List<Product> products = new ArrayList<>();
                for(int i = 0; i < 50; i++){
                    Product p = ProductGenerator.generateProduct();
                    productService.addProduct(p);
                    products.add(p);
                }

                if (orderService.getSize() == 0){
                    List<Order> orders = OrderGenerator.generateOrders(products, 50);
                    for (Order o : orders){
                        orderService.addOrder(o);
                    }
                }
            }
        } catch (RepositoryException e){
            throw new RuntimeException(e);
        }

        String appType = prop.getProperty("AppType", "console").toLowerCase();

        switch (appType) {
            case "gui" -> {
                HelloApplication.setServices(productService, orderService);
                Application.launch(HelloApplication.class, args);
            }
            case "console" -> {
                ConsoleUI ui = new ConsoleUI(orderService, productService);
                ui.start();
            }
            default -> throw new IllegalArgumentException("Unknown AppType: " + appType);
        }

    }
}
