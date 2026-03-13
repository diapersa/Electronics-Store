package com.example.a4diapersa.repository;

import com.example.a4diapersa.domain.Order;
import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.exceptions.ObjectNotFoundException;
import com.example.a4diapersa.exceptions.RepositoryException;
import javafx.scene.control.Alert;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SQLOrderRepository extends MemoryRepository<Order> implements AutoCloseable {

    private static final String JDBC_URL =
            "jdbc:sqlite:data/store.db";

    private Connection conn = null;

    private final IRepository<Product> productRepo;

    public SQLOrderRepository(IRepository<Product> productRepo) {
        this.productRepo = productRepo;
        openConnection();

        try (Statement st = conn.createStatement()){
            st.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        createSchema();
        loadData();
    }

    private void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(JDBC_URL);

            if (conn == null || conn.isClosed())
                conn = ds.getConnection();

        } catch (SQLException e) {
            throw new RuntimeException("Error opening connection to database from SQLOrderRepository.");
        }
    }

    private void createSchema() {
        try{
            try (final Statement statement = conn.createStatement()) {
                statement.executeUpdate("create table if not exists orders(id integer primary key autoincrement, deliveryDate datetime)");

                /// cream tabelul de relatie many-to-many unde stocam id-ul comenzii, id-ul produsului
                statement.executeUpdate("create table if not exists orderProducts(orderId int, productId int, foreign key(orderId) references orders(id), foreign key(productId) references Product(id))");
            }
        } catch (SQLException e){
            System.err.println("[Error] createSchema SQLOrderRepo: " + e.getMessage());
        }
    }

    private void loadData() {
        try (PreparedStatement statement = conn.prepareStatement("select * from orders");
             ResultSet rs = statement.executeQuery()){

            while (rs.next()) {
                int orderId = rs.getInt("id");
                LocalDate deliveryDate = rs.getDate("deliveryDate").toLocalDate();

                ArrayList<Product> products = new ArrayList<>();

                // luam toate id-urile produselor asociate comenzii curente
                try (PreparedStatement statement2 = conn.prepareStatement(
                        "select productId from orderProducts where orderId = ?")){

                    //  select productId from orderProducts where orderId = ?
                    //  vrem sa selectam toate liniile din orderProducts unde
                    // orderId e ceva - noi setam ce va fi!!

                    // 1 = primul ? din codul din sql
                    // orderId = valoarea pe care vreau sa o pun in locul lui ?

                    statement2.setInt(1, orderId);

                    try (ResultSet rs2 = statement2.executeQuery()) {
                        // rs2 e tabelul de rezultate
                        while (rs2.next()) {
                            int productId = rs2.getInt("productId");

                            // luam produsul din SQLProductRepo
                            try {
                                Product p = productRepo.getById(productId);

                                if (p != null) {
                                    products.add(p);
                                }
                            } catch (RepositoryException e){
                                System.out.println("Product does not exist");
                                continue;
                            }


                        }
                    }
                }

                Order order = new Order(orderId, products, deliveryDate);

                super.add(order);

            }

        } catch (SQLException | RepositoryException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void add(Order order) {
        super.add(order);

        // adaugam in baza de date
        try (PreparedStatement statement = conn.prepareStatement("insert into orders(deliveryDate) values (?)", Statement.RETURN_GENERATED_KEYS);){
            statement.setDate(1, Date.valueOf(order.getDeliveryDate()));
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            int orderId = rs.getInt(1);

            try (PreparedStatement statement2 = conn.prepareStatement("insert into orderProducts(orderId, productId) values (?, ?)");) {
                for (Product p: order.getProducts()){
                    statement2.setInt(1, orderId);
                    statement2.setInt(2, p.getProductID());
                    statement2.executeUpdate();
                }
            }

            try (ResultSet rs2 = statement.getGeneratedKeys()){
                if (rs2.next()) {
                    int id = rs2.getInt(1);
                    order.setID(id);
                }
            }

        } catch (SQLException e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String deleteOrderProducts = "delete from orderProducts where orderId = ?";
        String deleteOrder = "delete from orders where id = ?";


        try (PreparedStatement statement = conn.prepareStatement(deleteOrderProducts)) {
            statement.setInt(1, id);
            statement.executeUpdate();

            try (PreparedStatement statement2 = conn.prepareStatement(deleteOrder)) {
                statement2.setInt(1, id);
                int affected2 = statement2.executeUpdate();

                if (affected2 == 0){
                    throw new ObjectNotFoundException(id);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(Order entity) {
        delete(entity.getID());  // SQL delete
        super.delete(entity.getID());   // memory delete
    }

    @Override
    public void update(Order order) {
        String updateOrderSql =
                "update orders set deliveryDate = ? where id = ?";

        String deleteProductsSql =
                "delete from orderProducts where orderId = ?";

        String insertProductSql =
                "insert into orderProducts(orderId, productId) values (?, ?)";

        try {
            conn.setAutoCommit(false);

            // update orders table with new delivery date
            try (PreparedStatement stmt = conn.prepareStatement(updateOrderSql)){
                stmt.setDate(1, Date.valueOf(order.getDeliveryDate()));
                stmt.setInt(2, order.getID());

                int affected = stmt.executeUpdate();

                if (affected == 0){
                    throw new ObjectNotFoundException(order.getID());
                }
            }

            // delete ALL old product associations for this order
            try (PreparedStatement stmt = conn.prepareStatement(deleteProductsSql)){
                stmt.setInt(1, order.getID());
                stmt.executeUpdate();
            }

            // insert ALL new product associations for this order
            try (PreparedStatement stmt = conn.prepareStatement(insertProductSql)){
                for (Product p: order.getProducts()){
                    stmt.setInt(1, order.getID());

                    // Make sure we're using the correct product ID
                    int productId = p.getID();  // Use getID() which is the same as getProductID()
                    if (productId <= 0) {
                        throw new RepositoryException("Invalid product ID: " + productId);
                    }

                    stmt.setInt(2, productId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            super.update(order);

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RepositoryException("Error updating order: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (conn != null)
            conn.close();
    }
}












//
//
//@Override
//public void update(Order order, int productId) {
//    String updateOrderSql =
//            "update orders set deliveryDate = ? where id = ?";
//
//    String deleteProductsSql =
//            "delete from orderProducts where orderId = ? and productId = ?";
//
//    String insertProductSql =
//            "insert into orderProducts(orderId, productId) values (?, ?)";
//
//    try {
//        conn.setAutoCommit(false);
//
//        // update orders
//        try (PreparedStatement stmt = conn.prepareStatement(updateOrderSql)){
//            stmt.setDate(1, Date.valueOf(order.getDeliveryDate()));
//            stmt.setInt(2, order.getID());
//
//            int affected = stmt.executeUpdate();
//
//            if (affected == 0){
//                throw new ObjectNotFoundException(order.getID());
//            }
//        }
//
//        try (PreparedStatement stmt = conn.prepareStatement(insertProductSql)){
//            stmt.setInt(1, order.getID());
//            stmt.setInt(2, productId);
//
//            stmt.executeUpdate();
//        }
