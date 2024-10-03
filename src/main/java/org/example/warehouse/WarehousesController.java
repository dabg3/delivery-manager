package org.example.warehouse;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("warehouses")
public class WarehousesController {

    private final Delivery delivery;
    private final WarehouseRepository warehouseRepository;

    @Inject
    public WarehousesController(Delivery delivery,
                                WarehouseRepository warehouseRepository) {
        this.delivery = delivery;
        this.warehouseRepository = warehouseRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WarehouseDTO> getWarehouses() {
        PanacheQuery<WarehouseEntity> query = warehouseRepository.findAll();
        return query.stream()
                .map(WarehousesController::entityToDTO)
                .toList();
    }

    @Path("/{id}/routes")
    @POST
    public Response calculateDeliveryRoute(@PathParam("id") String id) {
        try {
            Long idL = Long.parseLong(id);
            return Response.ok(delivery.route(idL)).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    /*
     * TODO: seamless entity<->DTO conversion
     * https://modelmapper.org/
     * https://commons.apache.org/proper/commons-beanutils/
     */

    private static WarehouseDTO entityToDTO(WarehouseEntity warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        return dto;
    }

}
