package org.example.delivery.exception;

public class NoOrdersReadyException extends DeliveryException {

    private final static String message = "No orders ready for delivery";

    public NoOrdersReadyException() {
        super(message);
    }
}
