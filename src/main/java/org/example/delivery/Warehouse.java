package org.example.delivery;


import jakarta.persistence.*;

@Entity
public class Warehouse {

    @Id
    @SequenceGenerator(name="WAREHOUSE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WAREHOUSE_SEQ")
    private Long id;

    private String name;
    @Embedded
    private GeoPoint location;

    public Warehouse() {}

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
}
