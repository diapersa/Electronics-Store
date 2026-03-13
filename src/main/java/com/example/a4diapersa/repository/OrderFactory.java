package com.example.a4diapersa.repository;


import com.example.a4diapersa.domain.Order;
import com.example.a4diapersa.domain.Product;
import com.example.a4diapersa.exceptions.RepositoryException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class OrderFactory implements Factory<Order>{

    @Override
    public Order fromTokens(String[] tokens) {
        try {
            // format:  idOrder ; idProd1|categorie1|nume1|pret1 # idProd2|categorie2|nume2|pret2 ... ; deliveryDate
            // campurile din order sunt despartite prin ;
            // produsele sunt despartite prin #
            // campurile unui produs sunt despartite prin |

            if(tokens == null || tokens.length < 3){
                throw new RepositoryException("Invalid line for Order: " + Arrays.toString(tokens));
            }
            
            ArrayList<Product> products = new ArrayList<>();

            int id = Integer.parseInt(tokens[0].trim());
            String[] productTokens = tokens[1].split("#");
            
            // productTokens[0] = "idProd1|categorie1|nume1|pret1"
            // productTokens[1] = "idProd2|categorie2|nume2|pret2" ...

            for (String productToken : productTokens) {
                Product product = getProduct(productToken);
                products.add(product);
            }

            LocalDate date = LocalDate.parse(tokens[2].trim());

            return new Order(id, products, date);
        }
        catch (RepositoryException e) {
            throw e;
        }
    }

    private static Product getProduct(String productToken) {
        String[] fields = productToken.split("\\|");

        // fields[0] = idProduct
        // fields[1] = category
        // fields[2] = name
        // fields[3] = price

        int idProduct = Integer.parseInt(fields[0].trim());
        String productCategory = fields[1].trim();
        String productName = fields[2].trim();
        int price = Integer.parseInt(fields[3].trim());

        return new Product(idProduct, productCategory, productName, price);
    }

    @Override
    public String toLine(Order order) {
        // format:  idOrder ; idProd1|categorie1|nume1|pret1 # idProd2|categorie2|nume2|pret2 ... ; deliveryDate
        // campurile din order sunt despartite prin ;
        // produsele sunt despartite prin #
        // campurile unui produs sunt despartite prin |

        StringBuilder sb = new StringBuilder();

        sb.append(order.getID());
        sb.append(" ; ");

        ArrayList<Product> products = order.getProducts();

        for(int i = 0; i < products.size(); i++){
            Product product = products.get(i);

            sb.append(product.getID());
            sb.append("|");
            sb.append(product.getCategory());
            sb.append("|");
            sb.append(product.getProductName());
            sb.append("|");
            sb.append(product.getProductPrice());

            if(i != products.size() - 1)
                sb.append(" # ");
        }

        sb.append(" ; ");
        sb.append(order.getDeliveryDate());
        return sb.toString();
    }
}
