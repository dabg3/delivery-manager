package org.example.delivery;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

import static foreign.delivery.delivery_h.find_best_route;
import static foreign.delivery.delivery_h.print_route;


@Path("warehouses")
public class WarehousesController {

    private final WarehouseRepository warehouseRepository;

    @Inject
    public WarehousesController(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Warehouse> getWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.listAll();
        return warehouses;
    }

    @Path("/{id}/routes")
    @POST
    public List<Integer> calculateDeliveryRoute(@PathParam("id") String id) {
        PanacheQuery<Warehouse> deliverableOrders = warehouseRepository
                .find("select w from Warehouse w join fetch w.orders o where w.id = ?1 and o.state = 'ACCEPTED'",
                        id);
        Warehouse w = deliverableOrders.firstResult();
        // adding one because warehouse must be a node in the graph
        int nodesCount = 1 + w.getOrders().size();
        double[] coordinates = new double[nodesCount * 2];
        coordinates[0] = w.getLocation().getLatitude();
        coordinates[1] = w.getLocation().getLongitude();
        for (int i = 0; i < nodesCount - 1; i++) {
            coordinates[2 + i * 2] = w.getOrders().get(i).getDeliveryAddress().getLatitude();
            coordinates[3 + i * 2] = w.getOrders().get(i).getDeliveryAddress().getLongitude();
        }
        List<Integer> bestRuote = new ArrayList<>();
        try (Arena memory = Arena.ofConfined()) {
            MemorySegment coordsSeg = memory.allocateArray(ValueLayout.JAVA_DOUBLE, coordinates);
            MemorySegment routeSeg = memory.allocate(nodesCount * Integer.BYTES);
            find_best_route(nodesCount, coordsSeg, routeSeg);
            print_route(nodesCount, routeSeg);
            routeSeg.elements(ValueLayout.JAVA_INT)
                    .map(m -> m.get(ValueLayout.JAVA_INT, 0))
                    .forEach(bestRuote::add);
        }
        w.getOrders().forEach(o -> o.setState(ShoppingOrder.State.DELIVERY));
        warehouseRepository.flush();
        return bestRuote;
    }
}
