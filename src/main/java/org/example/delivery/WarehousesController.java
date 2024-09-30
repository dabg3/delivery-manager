package org.example.delivery;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

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
    public void calculateDeliveryRoute(@PathParam("id") String id) {
        PanacheQuery<Warehouse> deliverableOrders = warehouseRepository
                .find("select w from Warehouse w join fetch w.orders o where w.id = ?1 and o.state = 'ACCEPTED'",
                        id);
    }
}
