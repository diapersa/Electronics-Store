package com.example.a4diapersa.ui;

import com.example.a4diapersa.domain.MonthlyProfit;
import com.example.a4diapersa.domain.Order;
import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.service.OrderService;
import com.example.a4diapersa.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final OrderService orderService;
    private final ProductService productService;
    private final Scanner scanner;

    public ConsoleUI(OrderService orderService, ProductService productService){
        this.orderService = orderService;
        this.productService = productService;
        this.scanner = new Scanner(System.in);
    }

    public void start(){
        boolean running = true;

        while(running){
            printMenu();

            System.out.println("\nEnter an option:");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> addProduct();
                case 2 -> addOrder();
                case 3 -> updateProduct();
                case 4 -> updateOrder();
                case 5 -> deleteProduct();
                case 6 -> deleteOrder();
                case 7 -> displayProducts();
                case 8 -> displayOrders();
                case 9 -> nrOfProductsPerCategory();
                case 10 -> displayMonthlyProfit();
                case 11 -> printProductsByRevenue();
                case 0 -> {
                    running = false;
                    System.out.println("Exit");
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void printMenu(){
        System.out.println("    --- MENU ---    ");
        System.out.println("1. Add product");
        System.out.println("2. Add order");
        System.out.println("3. Update product");
        System.out.println("4. Update order");
        System.out.println("5. Delete product");
        System.out.println("6. Delete order");
        System.out.println("7. Display products");
        System.out.println("8. Display orders");
        System.out.println(" *** Reports *** ");
        System.out.println("9. Number of products per category");
        System.out.println("10. Most profitable months of the year");
        System.out.println("11. Products by revenue");
        System.out.println("0. Exit");
    }

    private void addProduct(){
        try{
            System.out.println("ID product: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Category product:");
            String category = scanner.nextLine();

            System.out.println("Name product:");
            String name = scanner.nextLine();

            System.out.println("Price product:");
            int price = scanner.nextInt();

            productService.addProduct(new Product(id, category, name, price));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void updateProduct(){
        try{
            displayProducts();

            System.out.println("ID product you want to update:");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Add the new fields\n");

            System.out.println("Category product:");
            String category = scanner.nextLine();

            System.out.println("Name product:");
            String name = scanner.nextLine();

            System.out.println("Price product:");
            int price = scanner.nextInt();

            productService.updateProduct(new Product(id, category, name, price));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteProduct(){
        try {
            displayProducts();

            System.out.println("ID product you want to delete:");
            int id = scanner.nextInt();
            scanner.nextLine();

            Product product = productService.getByID(id);
            productService.deleteProduct(product);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void displayProducts(){
        System.out.println("Product list:");
        List<Product> products = productService.getAllProducts();

        for(Product p: products)
            System.out.println(p);
        System.out.println("\n");
    }

    private void addOrder(){
        try {
            System.out.println("ID order: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            displayProducts();
            System.out.println("Number products you want to add:");
            int nrProducts = scanner.nextInt();
            scanner.nextLine();

            /// daca vreau sa adaug mai multe produse si un id nu e valid NU LE ADAUG DELOC

            ArrayList<Product> products = new ArrayList<>(List.of());

            if(nrProducts > 0){
                while(nrProducts > 0){
                    System.out.println("ID product you want:");
                    int idProduct = scanner.nextInt();
                    scanner.nextLine();

                    Product product = productService.getByID(idProduct);
                    products.add(product);

                    nrProducts--;
                }
                orderService.addOrder(new Order(id, products, LocalDate.now()));
            }
            else{
                System.out.println("Please enter a number greater than 0");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void printMenuOrderUpdate(){
        System.out.println("1. Add product");
        System.out.println("2. Delete product");
        System.out.println("0. Exit");
    }

    private void addProductToOrderList(Order order){
        try{
            ArrayList<Product> products;
            products = order.getProducts();

            System.out.println("Products from the order:");

            for(Product p: products){
                System.out.println(p);
            }

            System.out.println("\n Available products:");
            displayProducts();

            System.out.println("ID product you want to add:");
            int idProduct = scanner.nextInt();
            scanner.nextLine();

            if(productService.ifExists(idProduct)){
                Product p =  productService.getByID(idProduct);

                products.add(p);
                Order updatedOrder = new Order(order.getID(), products, LocalDate.now());
                orderService.updateOrder(updatedOrder);
                System.out.println("Order updated!!\n");
            }
            else
                System.out.println("Product does not exist");

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void deleteProductFromOrderList(Order order){
        try{
            ArrayList<Product> products;
            products = order.getProducts();

            ArrayList<Product> newProducts = new ArrayList<>();
            newProducts = order.getProducts();

            System.out.println("Products from the order:");

            for(Product p: products)
                System.out.println(p);

            System.out.println("ID product you want to delete:");
            int idProduct = scanner.nextInt();
            scanner.nextLine();

            if(orderService.ifProductExists(idProduct, order.getID())){
                try{
                    int posProduct = orderService.getPosListProduct(idProduct, order.getID());
                    newProducts.remove(orderService.getProductByPosition(posProduct, order.getID()));

                    Order updatedOrder = new Order(order.getID(), newProducts, LocalDate.now());
                    orderService.updateOrder(updatedOrder);

                    System.out.println("Product deleted!!\n");
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void updateOrder(){
        try {

            displayOrders();    // print all the orders to choose from

            System.out.println("ID order you want to update:");
            int idOrder = scanner.nextInt();
            scanner.nextLine();

            Order orderToUpdate = orderService.getById(idOrder);
            System.out.println("Order to update: \n" + orderToUpdate + "\n");

            boolean running = true;

            while(running){
                printMenuOrderUpdate();

                System.out.println("\nEnter an option:");
                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1 -> addProductToOrderList(orderToUpdate);
                    case 2 -> deleteProductFromOrderList(orderToUpdate);
                    case 0 -> {
                        running = false;
                    }
                    default -> System.out.println("Invalid option");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteOrder(){
        try{
            displayOrders();

            System.out.println("ID order you want to delete:");
            int id = scanner.nextInt();
            scanner.nextLine();

            Order order = orderService.getById(id);
            orderService.deleteOrder(order);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void displayOrders(){
        System.out.println("Orders list:");

        List<Order> orders = orderService.getAllOrders();
        for (Order o: orders)
            System.out.println(o.toString());
        System.out.println("\n");
    }

    private void nrOfProductsPerCategory() {
        System.out.println("Number of products per category:");
        Map<String, Integer> map = orderService.getNrOfProductsPerCategory();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String category = entry.getKey();
            Integer count = entry.getValue();

            System.out.println(category + " -> " + count);
        }
        System.out.println("\n");
    }

    private void displayMonthlyProfit() {
        System.out.println("Monthly profit: ");

        List<String> list = orderService.getMonthlyProfit()
                .stream()
                .map(Object::toString)
                .toList();

        for (String s : list)
            System.out.println(s);

        System.out.println("\n");
    }


    private void printProductsByRevenue() {
        System.out.println("Products by revenue:");

        List<String> list = orderService.getProductSortedByRevenue()
                .stream()
                .map(Object::toString)
                .toList();

        for (String s: list)
            System.out.println(s);

        System.out.println("\n");
    }
}




