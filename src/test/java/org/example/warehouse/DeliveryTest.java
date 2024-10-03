package org.example.warehouse;

import org.example.location.GeoPoint;
import org.example.shopping.ShoppingOrderEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeliveryTest {

    @Test
    public void coordinatesArrayConversion_warehouseEntityWithOrders_layout() {
        WarehouseEntity w = new WarehouseEntity();
        w.setLocation(GeoPoint.random());
        ShoppingOrderEntity so1 = new ShoppingOrderEntity();
        so1.setDeliveryAddress(GeoPoint.random());
        ShoppingOrderEntity so2 = new ShoppingOrderEntity();
        so2.setDeliveryAddress(GeoPoint.random());
        w.getOrders().add(so1);
        w.getOrders().add(so2);
        double[] expected = {
                w.getLocation().getLatitude(),
                w.getLocation().getLongitude(),
                so1.getDeliveryAddress().getLatitude(),
                so1.getDeliveryAddress().getLongitude(),
                so2.getDeliveryAddress().getLatitude(),
                so2.getDeliveryAddress().getLongitude()
        };

        double[] result = Delivery.asCoordinatesArray(w);

        Assertions.assertArrayEquals(expected, result);
    }
}
