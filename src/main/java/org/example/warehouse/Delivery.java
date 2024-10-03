package org.example.warehouse;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.example.shopping.ShoppingOrderEntity;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static foreign.delivery.delivery_h.find_best_route;
import static foreign.delivery.delivery_h.print_route;

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

    @Transactional
    public List<Integer> route(Long id) {
        // TODO warehouse.orders should be ordered by their id
        PanacheQuery<WarehouseEntity> deliverableOrders = warehouseRepository
                .find("select w from Warehouse w join fetch w.orders o where w.id = ?1 and o.state = 'ACCEPTED'",
                        id);
        WarehouseEntity w = deliverableOrders.firstResult();
        if (Objects.isNull(w)) {
            // TODO throw domain error
            return new ArrayList<>();
        }
        double[] coordinates = asCoordinatesArray(w);
        int nodesCount = coordinates.length;
        List<Integer> bestRuote = new ArrayList<>();
        try (Arena memory = Arena.ofConfined()) {
            MemorySegment coordsSeg = memory.allocateArray(ValueLayout.JAVA_DOUBLE, coordinates);
            MemorySegment routeSeg = memory.allocate(nodesCount * Integer.BYTES);
            find_best_route(nodesCount, coordsSeg, routeSeg);
            print_route(nodesCount, routeSeg);
            routeSeg.elements(ValueLayout.JAVA_INT)
                    .skip(1) // don't care about warehouse
                    .map(m -> m.get(ValueLayout.JAVA_INT, 0))
                    .map(n -> n - 1) // align indexes to warehouse.getOrders()
                    .forEach(bestRuote::add);
        }
        w.getOrders().forEach(o -> o.setState(ShoppingOrderEntity.State.DELIVERY));
        warehouseRepository.flush();
        return bestRuote;
    }

    private double[] asCoordinatesArray(WarehouseEntity w) {
        // adding one because warehouse must be a node in the graph
        double[] coordinates = new double[1 + w.getOrders().size() * 2];
        coordinates[0] = w.getLocation().getLatitude();
        coordinates[1] = w.getLocation().getLongitude();
        for (int i = 0; i < w.getOrders().size(); i++) {
            coordinates[2 + i * 2] = w.getOrders().get(i).getDeliveryAddress().getLatitude();
            coordinates[3 + i * 2] = w.getOrders().get(i).getDeliveryAddress().getLongitude();
        }
        return coordinates;
    }
}
