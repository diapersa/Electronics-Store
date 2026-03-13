package com.example.a4diapersa;

import com.example.a4diapersa.service.OrderService;
import com.example.a4diapersa.service.ProductService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static ProductService productService;
    private static OrderService orderService;

    public static void setServices(ProductService productService, OrderService orderService) {
        HelloApplication.productService = productService;
        HelloApplication.orderService = orderService;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        // Aici se pot initializa Repository / Service etc.

//        IRepository<Product> repoProduct = new SQLProductRepository();
//        IRepository<Order> repoOrder = new SQLOrderRepository(repoProduct);

        // fxmlLoader.load() creeaza obiectul controller
        HelloController hc = fxmlLoader.getController();

        // Comunicarea o facem prin interfete, nu prin implementari!
//        hc.init(repoProduct, repoOrder);
        hc.init(productService, orderService);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
