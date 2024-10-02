package org.example.delivery;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Path("orders")
public class OrdersController {

    private final WarehouseRepository warehouseRepository;
    private final OrderRepository orderRepository;

    @Inject
    public OrdersController(WarehouseRepository warehouseRepository,
                            OrderRepository orderRepository) {
        this.warehouseRepository = warehouseRepository;
        this.orderRepository = orderRepository;
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrder(ShoppingOrderDTO orderDTO) {
        // TODO use dto
        if (Objects.isNull(orderDTO) || Objects.isNull(orderDTO.getDeliveryAddress())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // TODO move domain stuff on its own
        PanacheQuery<Warehouse> query = warehouseRepository
                .find("select w from Warehouse w where function('distance', w.location.latitude, w.location.longitude, ?1, ?2) < 30000",
                        orderDTO.getDeliveryAddress().getLatitude(),
                        orderDTO.getDeliveryAddress().getLongitude());
        List<Warehouse> warehouses = query.list();
        if (warehouses.isEmpty()) {
            // TODO: return domain error, no warehouses available
        }
        // TODO: sort via query orderBy
        warehouses.sort(Comparator.comparingInt(w -> w.getOrders().size()));
        Warehouse selected = warehouses.get(0);
        selected.getOrders().add(DTOToEntity(orderDTO));
        warehouseRepository.flush();
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingOrderDTO> getOrders() {
        // TODO filtering, ordering
        PanacheQuery<ShoppingOrder> query = orderRepository.findAll();
        return query.stream()
                .map(OrdersController::entityToDTO)
                .toList();
    }


    @Path("/{id}")
    @PATCH
    @Transactional
    public Response editOrder(@PathParam("id") Long id, ShoppingOrderDTO orderDTO) {
        if (Objects.isNull(orderDTO) || Objects.isNull(orderDTO.getDeliveryAddress())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        ShoppingOrder order = orderRepository.findById(id);
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        orderRepository.flush();
        return Response.ok().build();
    }

    private static ShoppingOrder DTOToEntity(ShoppingOrderDTO dto) {
        ShoppingOrder order = new ShoppingOrder();
        try {
            BeanUtils.copyProperties(order, dto);
        } catch (IllegalAccessException | InvocationTargetException _) {
            // unreachable
        }
        return order;
    }

    private static ShoppingOrderDTO entityToDTO(ShoppingOrder order) {
        ShoppingOrderDTO dto = new ShoppingOrderDTO();
        try {
            BeanUtils.copyProperties(dto, order);
        } catch (IllegalAccessException | InvocationTargetException _) {
            // unreachable
        }
        return dto;
    }
}
