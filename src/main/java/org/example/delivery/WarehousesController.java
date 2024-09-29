package org.example.delivery;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("warehouses")
public class WarehousesController {

    private WarehouseRepository warehouseRepository;

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

    @Path("/{id}/deliveries")
    @POST
    public void calculateDeliveryRoute(@PathParam("id") String id) {

    }
}
