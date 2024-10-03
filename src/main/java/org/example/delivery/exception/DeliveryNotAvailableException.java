package org.example.delivery.exception;

public class DeliveryNotAvailableException extends DeliveryException {

    private static final String message = "Delivery not available to specified address";

    public DeliveryNotAvailableException() {
        super(message);
    }
}
