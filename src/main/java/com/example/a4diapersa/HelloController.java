package com.example.a4diapersa;

import com.example.a4diapersa.domain.Order;
import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.exceptions.RepositoryException;
import com.example.a4diapersa.repository.IRepository;
import com.example.a4diapersa.service.OrderService;
import com.example.a4diapersa.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.security.AllPermission;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelloController {
    public ListView<Product> listProducts;

    // pentru produse
    public TextField textProductCategory;
    public TextField textProductName;
    public TextField textProductPrice;


    public HBox hboxButtonsProduct;
    public Button btnAddProduct;
    public Button btnDeleteProduct;
    public Button btnUpdateProduct;

    // pentru orders
    public ListView<Order> listOrders;

    public TextField textDeliveryDateOrder;
    public TextField textIdProductForOrder;
    public HBox hboxButtonsOrder;
    public Button btnAddOrder;
    public Button btnAddProductToOrder;
    public ListView<Product> listOrderProducts;

    public List<Product> currentOrderProducts = new ArrayList<>();
    public Button btnDeleteOrder;
    public Button btnUpdateOrder;
    public HBox hboxBtnReports;

    // reports
    public Button btnNrOfProductsPerCategory;
    public ListView<String> listReports;
    public Button btnMonthlyProfit;
    public Button btnProductRevenue;
    public Button btnDeleteProductFromOrder;
    public Button btnAddProductToExistingOrder;


    // productsData este o lista observabila de produse
    // productsData reprezinta sursa de date pentru ListView<Product>
    // FXCollections.observableArrayList() -> returneaza o lista "observabila"
    // Obervabila = ??
    //  -> ListView se aboneaza la modificarile listei productsData
    //  -> cand se adauga un element in lista => ListView este notificat => ListView se redeseaneaza
    //  -> la fel si la stergere
    ObservableList<Product> productsData = FXCollections.observableArrayList();

    ObservableList<Order> ordersData = FXCollections.observableArrayList();

    ObservableList<Product> orderProductsData = FXCollections.observableArrayList();


    /*
    Peste tot unde e posibil, incercam sa folosim cea mai generica interfata disponibila
    (ex: java.util.Collection, sau IRepository in cazul nostru)
    Asta permite utilizarea oricarei implementari de repo, practic dezlegam comportamentul clasei
    HelloController de implementarea de repository
     */
    private ProductService productService;
    private OrderService orderService;

    public void init(ProductService productService, OrderService orderService) {
        // legam componenta grafica de sursa ei de date
        listProducts.setItems(productsData);
        listOrders.setItems(ordersData);

        this.productService = productService;
        this.orderService = orderService;

        productsData.addAll(productService.getAllProducts());
        ordersData.addAll(orderService.getAllOrders());
//        listOrderProducts.setItems(orderProductsData);

        listProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                onProductSelected();
            }
        });

        listOrders.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        onOrderSelected();
                    }
                });

        listOrderProducts.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        onOrderProductSelected();
                    }
                });
    }

    public void onProductSelected() {
        Product product = listProducts.getSelectionModel().getSelectedItem();

        if (product != null && productsData.contains(product)) {
            textProductCategory.setText(product.getCategory());
            textProductName.setText(product.getProductName());
            textProductPrice.setText(String.valueOf(product.getProductPrice()));
        } else{
            // daca produsul nu mai exista, curatam campurile
            textProductCategory.clear();
            textProductName.clear();
            textProductPrice.clear();
        }
    }

    @FXML
    protected void onAddProductButtonClick() {
        // Incercam sa adaugam un Product in lista

        try{
            String category = textProductCategory.getText();
            String name = textProductName.getText();
            int price = Integer.parseInt(textProductPrice.getText());

            var product = new Product(category, name, price);

            /*
            Adaugarea in repo s-ar putea sa rezulte in exceptii (ex: ID duplicat, ceva constrangere pe care
            nu o cunoastem in UI, fisierul de stocare nu se poate actualiza in repo, nu avem conexiune cu
            baza de date etc.)
            => in caz de exceptie, nu avem voie sa adaugam in lista din UI

            => Daca avem exceptie din service/repo, nu se mai adauga in lista din UI
             */

            productService.addProduct(product);
            productsData.add(product);

        } catch (NumberFormatException | RepositoryException e){
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.showAndWait();

            textProductCategory.clear();
            textProductName.clear();
            textProductPrice.clear();
        }
    }

    @FXML
    protected void onAddOrderButtonClick() {
        try{
            if (currentOrderProducts.isEmpty()){
                Alert error = new Alert(Alert.AlertType.ERROR, "Please select at least one product", ButtonType.OK);
                error.showAndWait();
                return;
            }

            LocalDate deliveryDate = LocalDate.parse(textDeliveryDateOrder.getText());

            Order newOrder = new Order();
            newOrder.setDeliveryDate(deliveryDate);
            newOrder.setProducts(new ArrayList<>(currentOrderProducts));  // copiem lista

            // salvam comanda in service -> repo
            orderService.addOrder(newOrder);
            ordersData.add(newOrder);

            currentOrderProducts.clear();
            listOrderProducts.getItems().clear();
            textDeliveryDateOrder.clear();
            textIdProductForOrder.clear();

            Alert info = new Alert(Alert.AlertType.INFORMATION, "Order added successfully!", ButtonType.OK);
            info.showAndWait();


        } catch (NumberFormatException | RepositoryException e){
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            error.showAndWait();

            textIdProductForOrder.clear();
            textDeliveryDateOrder.clear();
        }
    }

    public void onAddProductToOrderBtnClick() {
        try {
            int idProduct = Integer.parseInt(textIdProductForOrder.getText());

            // cautam produsul in repo
            Product product = productService.getByID(idProduct);

            if (product == null){
                Alert error = new Alert(Alert.AlertType.ERROR, "Product does not exist!", ButtonType.OK);
                error.showAndWait();
                return;
            }

            // Adaugam produsul in comanda curenta
            if (!currentOrderProducts.contains(product)){
                currentOrderProducts.add(product);
            }
            else {
                Alert err = new Alert(Alert.AlertType.INFORMATION, "Product was already added to the order", ButtonType.OK);
                err.showAndWait();
            }

            // actualizam ListView pt produsele dintr-o comanda

            listOrderProducts.getItems().setAll(currentOrderProducts);
            textIdProductForOrder.clear();

        } catch (NumberFormatException | RepositoryException e){
            Alert err = new Alert(Alert.AlertType.ERROR, "ID must be a number!", ButtonType.OK);
            err.showAndWait();
        }
    }

    public void onAddProductToExistingOrderBtnClick() {
        var selectedOrder = listOrders.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            Alert err = new Alert(Alert.AlertType.WARNING, "Please select an order first!", ButtonType.OK);
            err.showAndWait();
            return;
        }

        if (textIdProductForOrder.getText().trim().isEmpty()) {
            Alert err = new Alert(Alert.AlertType.WARNING, "Please enter a product ID!", ButtonType.OK);
            err.showAndWait();
            return;
        }

        try {
            int idProduct = Integer.parseInt(textIdProductForOrder.getText());

            // Search for the product in the repository
            Product product = productService.getByID(idProduct);

            if (product == null) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Product does not exist!", ButtonType.OK);
                error.showAndWait();
                return;
            }

            // Add the product to the selected order
            if (!selectedOrder.getProducts().contains(product)) {
                selectedOrder.getProducts().add(product);

                // Update the order in the service
                orderService.updateOrder(selectedOrder);

                // Refresh the product list for the selected order in the UI
                listOrderProducts.getItems().add(product);

                // Refresh the orders list to reflect the change (forces ListView to redraw)
                int selectedIndex = listOrders.getSelectionModel().getSelectedIndex();
                ordersData.set(selectedIndex, selectedOrder);

                textIdProductForOrder.clear();

                Alert info = new Alert(Alert.AlertType.INFORMATION, "Product added to order successfully!", ButtonType.OK);
                info.showAndWait();
            } else {
                Alert err = new Alert(Alert.AlertType.INFORMATION, "Product already exists in this order!", ButtonType.OK);
                err.showAndWait();
            }

        } catch (NumberFormatException e) {
            Alert err = new Alert(Alert.AlertType.ERROR, "ID must be a number!", ButtonType.OK);
            err.showAndWait();
        } catch (RepositoryException e) {
            Alert err = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
    }


    public void onDeleteProductBtnClick() {
        var product = listProducts.getSelectionModel().getSelectedItem();

        try {
            productService.deleteById(product.getProductID());
            productsData.remove(product);

        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
    }

    public void onDeleteOrderBtnClick() {
        var order = listOrders.getSelectionModel().getSelectedItem();
        
        try {
            orderService.deleteById(order.getOrderID());
            ordersData.remove(order);
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
        
    }

    public void onUpdateProductBtnClick() {
        var selected = listProducts.getSelectionModel().getSelectedItem();

        if (selected == null)
            return;

        selected.setCategory(textProductCategory.getText());
        selected.setProductName(textProductName.getText());
        selected.setProductPrice(Integer.parseInt(textProductPrice.getText()));

        try {
            productService.updateProduct(selected);

            // pt ca ObservableList<Product> sa se actualizeze
            int index = productsData.indexOf(selected);
            productsData.set(index, selected);  // actualizam exact obiectul

            // curatam campurile
            textProductCategory.clear();
            textProductName.clear();
            textProductPrice.clear();

        } catch (RepositoryException e){
            Alert err = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
    }

    public void onOrderSelected() {
        Order order = listOrders.getSelectionModel().getSelectedItem();

        if (order != null && ordersData.contains(order)) {
            textDeliveryDateOrder.setText(order.getDeliveryDate().toString());
            listOrderProducts.getItems().setAll(order.getProducts());
            orderProductsData.setAll(order.getProducts());
        }
    }

    public void onOrderProductSelected() {
        Product product = listOrderProducts.getSelectionModel().getSelectedItem();

        if (product != null && productsData.contains(product)) {
            textIdProductForOrder.setText(String.valueOf(product.getProductID()));
        }
    }

    public void nrOfProductsPerCategory() {
        Map<String, Integer> map = orderService.getNrOfProductsPerCategory();

        ObservableList<String> items = FXCollections.observableArrayList(
                map.entrySet().stream()
                        .map(e -> e.getKey() + " -> " + e.getValue())
                        .toList()
        );

        listReports.setItems(items);
    }

    public void onPrintMonthlyProfit() {
        listReports.setItems(
                FXCollections.observableArrayList(
                        orderService.getMonthlyProfit()
                                .stream()
                                .map(Object::toString)
                                .toList()
                )
        );
    }


    public void onPrintProductsByRevenue() {
        listReports.setItems(
                FXCollections.observableArrayList(
                        orderService.getProductSortedByRevenue()
                                .stream()
                                .map(Object::toString)
                                .toList()
                )
        );
    }

    public void onDeleteProductFromOrderBtnClick() {
        var selectedOrder = listOrders.getSelectionModel().getSelectedItem();
        var selectedProduct = listOrderProducts.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            Alert err = new Alert(Alert.AlertType.WARNING, "Please select an order first!", ButtonType.OK);
            err.showAndWait();
            return;
        }

        if (selectedProduct == null) {
            Alert err = new Alert(Alert.AlertType.WARNING, "Please select a product to delete!", ButtonType.OK);
            err.showAndWait();
            return;
        }

        try {
            // Remove the product from the order
            selectedOrder.getProducts().remove(selectedProduct);

            // Update the order in the service
            orderService.updateOrder(selectedOrder);

            // Refresh the UI
            listOrderProducts.getItems().remove(selectedProduct);

            // Refresh the orders list to reflect the change
            int selectedIndex = listOrders.getSelectionModel().getSelectedIndex();
            ordersData.set(selectedIndex, selectedOrder);

            Alert info = new Alert(Alert.AlertType.INFORMATION, "Product removed from order successfully!", ButtonType.OK);
            info.showAndWait();

        } catch (RepositoryException e) {
            Alert err = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
    }
}
