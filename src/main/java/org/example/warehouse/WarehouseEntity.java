package org.example.warehouse;


import jakarta.persistence.*;
import org.example.location.GeoPoint;
import org.example.shopping.ShoppingOrderEntity;

import java.util.ArrayList;
import java.util.List;

@Entity(name="Warehouse")
public class WarehouseEntity {

    @Id
    @SequenceGenerator(name="WAREHOUSE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WAREHOUSE_SEQ")
    private Long id;

    private String name;
    @Embedded
    private GeoPoint location;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="warehouse_id", nullable = false)
    private List<ShoppingOrderEntity> orders = new ArrayList<>();

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

    public List<ShoppingOrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<ShoppingOrderEntity> orders) {
        this.orders = orders;
    }
}
