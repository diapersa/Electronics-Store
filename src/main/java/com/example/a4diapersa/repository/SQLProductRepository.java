package com.example.a4diapersa.repository;

import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.exceptions.ObjectNotFoundException;
import com.example.a4diapersa.exceptions.RepositoryException;
import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class SQLProductRepository extends MemoryRepository<Product> implements AutoCloseable{

    // Avem nevoie de un string de conectare la baza de date

    private static final String JDBC_URL =
            "jdbc:sqlite:data/store.db";

    // static variable -> automatically assigned default values when your class is loaded
    // final variable -> constant = variable whose value we can't change after it's been initialized

    // Avem nevoie de o conexiune la baza de date
    // Asta o obtinem folosind JDBC
    private Connection conn = null;

    public SQLProductRepository(){
        // obtinem conexiunea la baza de date
        openConnection();

        // cream tabelele din baza de date (daca ele nu exista deja)
        // (optional)
        createSchema();

        // de incarcat datele din baza de date
        loadData();
    }

    private void openConnection(){
        try{
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(JDBC_URL);

            if(conn == null || conn.isClosed())
                conn = ds.getConnection();

        }catch (SQLException e){
            throw new RuntimeException("Error opening connection to database from SQLProductRepository.");
        }

    }

    private void createSchema(){
        try{

            // se creeaza un obiect Statement folosind conexiunea conn
            // try (...) -> try-with-resources inchide automat statement dupa executie
            // final -> referinta nu poate fi schimbata

            try (final Statement statement = conn.createStatement()){
                // se executa comanda sql
                statement.executeUpdate("create table if not exists product(id integer primary key autoincrement, category text not null, name text not null, price integer not null)");
            }
        } catch (SQLException e){
            System.err.println("[Error] createSchema SQLProductRepo: " + e.getMessage());
        }

    }

    private void loadData(){
        try (PreparedStatement statement = conn.prepareStatement("select * from product");
        ResultSet rs = statement.executeQuery();){
            while (rs.next()){
                Product p = new Product(rs.getInt("id"), rs.getString("category"),
                        rs.getString("name"), rs.getInt("price"));

                super.add(p);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        } catch (RepositoryException e){
            throw new RuntimeException();
        }
    }

    @Override
    public void add(Product p) {
        super.add(p);

        // adaugam in baza de date
        try (PreparedStatement statement = conn.prepareStatement("insert into product(category, name, price) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
            // id NU e cheie primara
//            statement.setInt(1, p.getProductID());
//            statement.setString(2, p.getCategory());
//            statement.setString(3, p.getProductName());
//            statement.setInt(4, p.getProductPrice());

            // id primary key

            statement.setString(1, p.getCategory());
            statement.setString(2, p.getProductName());
            statement.setInt(3, p.getProductPrice());

            statement.executeUpdate();

            // obtinem ID-ul generat
            try (ResultSet rs = statement.getGeneratedKeys()){
                if (rs.next()){
                    int id = rs.getInt(1);
                    p.setID(id);
                }
            }

        } catch (SQLException e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "delete from product where id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            int affected = statement.executeUpdate();

            if (affected == 0 ){
                throw new ObjectNotFoundException(id);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Product entity) {
        super.delete(entity.getID());
    }

    @Override
    public void update(Product product) {
        String sql = "update product set category = ?, name = ?, price = ? where id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, product.getCategory());
            statement.setString(2, product.getProductName());
            statement.setInt(3, product.getProductPrice());
            statement.setInt(4, product.getID());

            int affected = statement.executeUpdate();

            if (affected == 0 ){
                throw new ObjectNotFoundException(product.getID());
            }

            // Dupa ce db a fost actualizata, vom actualiza si in memorie
            super.update(product);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (conn != null)
            conn.close();
    }
}
