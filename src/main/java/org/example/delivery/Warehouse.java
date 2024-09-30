package org.example.delivery;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class Warehouse {

    @Id
    @SequenceGenerator(name="WAREHOUSE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WAREHOUSE_SEQ")
    private Long id;

    private String name;
    @Embedded
    private GeoPoint location;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="warehouse_id", nullable = false)
    private List<ShoppingOrder> orders;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public List<ShoppingOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ShoppingOrder> orders) {
        this.orders = orders;
    }
}
