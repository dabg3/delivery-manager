package org.example.delivery;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class ShoppingOrder {

    @Id
    @SequenceGenerator(name="ORDER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_SEQ")
    private Long id;

    @Embedded
    private GeoPoint deliveryAddress;

    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany
    @JoinColumn(name="order_id", nullable=false)
    private List<ProductDetail> products;

    @Column(name="submission_date", nullable = false)
    private LocalDateTime submissionDate;

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
