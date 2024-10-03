package org.example.shopping;

import jakarta.persistence.*;
import org.example.location.GeoPoint;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name="ShoppingOrder")
public class ShoppingOrderEntity {

    @Id
    @SequenceGenerator(name="ORDER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_SEQ")
    private Long id;

    @Embedded
    private GeoPoint deliveryAddress;

    @Enumerated(EnumType.STRING)
    private State state = State.ACCEPTED;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name="order_id", nullable=false)
    private List<ProductDetail> products;

    // https://stackoverflow.com/questions/43414221/java-entity-storing-dates
    @Column(name="submission_date", nullable = false)
    private LocalDateTime submissionDate = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GeoPoint getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(GeoPoint deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<ProductDetail> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDetail> products) {
        this.products = products;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public enum State {
        ACCEPTED, DELIVERY
    }

    @Entity(name="OrderProductsDetail")
    public static class ProductDetail {

        @Id
        @SequenceGenerator(name="PRODUCT_DETAIL_SEQ", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_DETAIL_SEQ")
        private Long id;

        @Column(name = "product_id", nullable = false)
        private String productId;

        @Column(nullable = false)
        private int quantity;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
