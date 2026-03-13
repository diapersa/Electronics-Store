package com.example.a4diapersa.service;


import com.example.a4diapersa.domain.MonthlyProfit;
import com.example.a4diapersa.domain.Order;
import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.domain.ProductRevenue;
import com.example.a4diapersa.exceptions.ObjectNotFoundException;
import com.example.a4diapersa.exceptions.RepositoryException;
import com.example.a4diapersa.repository.IRepository;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class OrderService {
//    private final MemoryRepository<Order> repository;
    private final IRepository<Order> repository;

    public OrderService(IRepository<Order> repository) {
        this.repository = repository;
    }

    public void addOrder(Order order) {
        repository.add(order);
    }

    public Order getOrder(Order order) {
        return repository.find(order);
    }

    public void deleteOrder(Order order) {
        repository.delete(order);
    }

    public void deleteById(int id) {
        repository.delete(id);
    }

    public void updateOrder(Order order) {
        repository.update(order);
    }

    public void addProductByUpdate(Order order, int productId) {
        repository.update(order, productId);
    }

    public List<Order> getAllOrders(){
        return repository.getAll();
    }

    public Order getById(int ID) throws RepositoryException {
        return repository.getById(ID);
    }

    public int getPosListProduct(int idProduct, int idOrder) {
        Order order = getById(idOrder);
        ArrayList<Product> products = order.getProducts();

        for(int i = 0; i < products.size(); i++)
            if(products.get(i).getID() == idProduct)
                return i;

        throw new ObjectNotFoundException(idProduct);
    }

    public Product getProductByPosition(int posProduct, int idOrder) {
        Order order = getById(idOrder);
        ArrayList<Product> products = order.getProducts();
        return products.get(posProduct);
    }

    public boolean ifProductExists(int idProduct, int idOrder) {
        Order order = getById(idOrder);
        ArrayList<Product> products = order.getProducts();

        for (Product product : products)
            if (product.getID() == idProduct)
                return true;

        return false;
    }

    public int getSize(){
        return repository.getSize();
    }

//    Numărul de produse comandate din fiecare categorie.
//    Se vor afișa numele fiecărei categorii
//    precum și numărul de produse comandate din acea categorie,
//    în ordine descrescătoare a numărului de comenzi.

    public Map<String, Integer> getNrOfProductsPerCategory(){
        return repository.getAll().stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.summingInt(product ->1 )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

//    Cele mai profitabile luni ale anului. Se vor afișa lunile anului,
//    împreună cu numărul de comenzi din fiecare lună precum
//    și prețul total al produselor comandate.

    public List<MonthlyProfit> getMonthlyProfit(){
        return repository.getAll().stream()
                .collect(Collectors.groupingBy(
                        order -> order.getDeliveryDate().getMonth()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Month month = entry.getKey();
                    List<Order> orders = entry.getValue();

                    int ordersCount = orders.size();

                    double totalPrice = orders.stream()
                            .flatMap(order -> order.getProducts().stream())
                            .mapToDouble(Product::getProductPrice)
                            .sum();

                    return new MonthlyProfit(month, ordersCount, totalPrice);
                })
                .sorted(Comparator.comparingDouble(MonthlyProfit::getTotalPrice).reversed())
                .toList();
    }



//    Lista de produse, sortată descrescător după încasările
//    realizate. Încasările pentru un produs reprezintă prețul
//    produsului înmulțit cu numărul total de produse comandate.

    public List<ProductRevenue> getProductSortedByRevenue() {
        return repository.getAll().stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.groupingBy(
                        Product::getProductID,
                        Collectors.toList()
                ))
                .values().stream()
                .map(products -> {
                    Product product = products.getFirst();
                    int totalQuantity = products.size();
                    return new ProductRevenue(product, totalQuantity);
                })
                .sorted(Comparator.comparingDouble(ProductRevenue::getRevenue).reversed())
                .toList();
    }

}
