package org.example.delivery;

import java.util.List;

public class ShoppingOrder {

    private GeoPoint deliveryAddress;
    private List<Product> products;
    private State state;

    public static class Product {
        private String id;
        private int quantity;
    }

    public enum State {
        ACCEPTED, DELIVERY
    }
}
