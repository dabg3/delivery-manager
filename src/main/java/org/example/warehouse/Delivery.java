package org.example.warehouse;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.example.shopping.ShoppingOrderEntity;

import java.util.Comparator;
import java.util.List;

@Dependent
public class Delivery {

    private Long maxDistanceMeters;
    private WarehouseRepository warehouseRepository;

    @Inject
    Delivery(@ConfigProperty(name = "delivery.max-distance-meters") Long maxDistanceMeters,
             WarehouseRepository warehouseRepository) {
        this.maxDistanceMeters = maxDistanceMeters;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public void register(ShoppingOrderEntity order) {
        // TODO: if deliveryAddress is the same of another already registered order, merge them
        assignWarehouse(order).getOrders().add(order);
        warehouseRepository.flush();
    }

    private WarehouseEntity assignWarehouse(ShoppingOrderEntity order) {
        PanacheQuery<WarehouseEntity> query = warehouseRepository
                .find("select w from Warehouse w where function('distance', w.location.latitude, w.location.longitude, ?1, ?2) < ?3",
                        order.getDeliveryAddress().getLatitude(),
                        order.getDeliveryAddress().getLongitude(),
                        maxDistanceMeters);
        List<WarehouseEntity> warehouses = query.list();
        if (warehouses.isEmpty()) {
            // TODO: return domain error, no warehouses available
            return null;
        }
        // TODO: sort via query orderBy
        warehouses.sort(Comparator.comparingInt(w -> w.getOrders().size()));
        return warehouses.getFirst();
    }

    public void route() {

    }
}
