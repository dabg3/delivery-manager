package org.example.delivery.warehouse;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.example.delivery.exception.DeliveryNotAvailableException;
import org.example.delivery.exception.NoOrdersReadyException;
import org.example.delivery.order.ShoppingOrderEntity;

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
    public void register(ShoppingOrderEntity order) throws DeliveryNotAvailableException {
        assignWarehouse(order).getOrders().add(order);
        warehouseRepository.flush();
    }

    private WarehouseEntity assignWarehouse(ShoppingOrderEntity order) throws DeliveryNotAvailableException {
        PanacheQuery<WarehouseEntity> query = warehouseRepository
                .find("select w from Warehouse w where function('distance', w.location.latitude, w.location.longitude, ?1, ?2) < ?3",
                        order.getDeliveryAddress().getLatitude(),
                        order.getDeliveryAddress().getLongitude(),
                        maxDistanceMeters);
        List<WarehouseEntity> warehouses = query.list();
        if (warehouses.isEmpty()) {
            throw new DeliveryNotAvailableException();
        }
        // TODO: sort via query orderBy
        warehouses.sort(Comparator.comparingInt(w -> w.getOrders().size()));
        return warehouses.getFirst();
    }

    @Transactional
    public List<ShoppingOrderEntity> route(Long id) throws NoOrdersReadyException {
        WarehouseEntity w = retrieveDeliverableOrders(id);
        List<ShoppingOrderEntity> bestRoute = findBestRoute(w);
        bestRoute.forEach(o -> o.setState(ShoppingOrderEntity.State.DELIVERY));
        warehouseRepository.flush();
        return bestRoute;
    }

    private WarehouseEntity retrieveDeliverableOrders(Long id) throws NoOrdersReadyException {
        PanacheQuery<WarehouseEntity> deliverableOrders = warehouseRepository
                .find("select w from Warehouse w join fetch w.orders o where w.id = ?1 and o.state = 'ACCEPTED'",
                        id);
        WarehouseEntity w = deliverableOrders.firstResult();
        if (Objects.isNull(w)) {
            throw new NoOrdersReadyException();
        }
        return w;
    }

    // It is not always the best route actually,
    // it may be 'optimal' according to a set of heuristics
    private List<ShoppingOrderEntity> findBestRoute(WarehouseEntity w) {
        double[] coordinates = asCoordinatesArray(w);
        int nodesCount = coordinates.length / 2;
        List<ShoppingOrderEntity> bestRoute = new ArrayList<>();
        try (Arena memory = Arena.ofConfined()) {
            MemorySegment coordsSeg = memory.allocateArray(ValueLayout.JAVA_DOUBLE, coordinates);
            MemorySegment routeSeg = memory.allocate(nodesCount * Integer.BYTES);
            find_best_route(nodesCount, coordsSeg, routeSeg);
            print_route(nodesCount, routeSeg);
            routeSeg.elements(ValueLayout.JAVA_INT)
                    .skip(1) // don't care about warehouse
                    .map(m -> m.get(ValueLayout.JAVA_INT, 0))
                    .map(n -> n - 1) // align indexes to warehouse.getOrders()
                    .map(w.getOrders()::get)
                    .forEach(bestRoute::add);
        }
        return bestRoute;
    }

    static double[] asCoordinatesArray(WarehouseEntity w) {
        // adding one because warehouse must be a node in the graph
        double[] coordinates = new double[(1 + w.getOrders().size()) * 2];
        coordinates[0] = w.getLocation().getLatitude();
        coordinates[1] = w.getLocation().getLongitude();
        for (int i = 0; i < w.getOrders().size(); i++) {
            coordinates[2 + i * 2] = w.getOrders().get(i).getDeliveryAddress().getLatitude();
            coordinates[3 + i * 2] = w.getOrders().get(i).getDeliveryAddress().getLongitude();
        }
        return coordinates;
    }
}
