package org.example.shopping;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.location.GeoPoint;

import java.time.LocalDateTime;
import java.util.List;

public class ShoppingOrderDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;

    private GeoPoint deliveryAddress;

    private String state;

    private List<ProductDetailDTO> products;

    private LocalDateTime submissionDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String warehouse;

    public ShoppingOrderDTO() {}

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<ProductDetailDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDetailDTO> products) {
        this.products = products;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public static class ProductDetailDTO {

        private String productId;

        private int quantity;

        public ProductDetailDTO() {}

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
