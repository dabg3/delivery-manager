package org.example.delivery.warehouse;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.delivery.exception.NoOrdersReadyException;
import org.example.delivery.order.ShoppingOrderDTO;
import org.example.delivery.order.ShoppingOrderEntity;

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
    public Response calculateDeliveryRoute(@PathParam("id") Long id) throws NoOrdersReadyException {
        List<ShoppingOrderDTO> routedOrders = delivery.route(id).stream()
                .map(WarehousesController::entityToDTO)
                .toList();
        return Response.ok(routedOrders).build();
    }

    /*
     * TODO: seamless entity<->DTO conversion
     * https://modelmapper.org/
     * https://commons.apache.org/proper/commons-beanutils/
     * https://mapstruct.org/
     */

    private static WarehouseDTO entityToDTO(WarehouseEntity warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        return dto;
    }

    private static ShoppingOrderDTO entityToDTO(ShoppingOrderEntity order) {
        ShoppingOrderDTO dto = new ShoppingOrderDTO();
        dto.setId(order.getId());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        return dto;
    }

}
