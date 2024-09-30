package org.example.delivery;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    public Response addOrder(ShoppingOrder order) {
        if (Objects.isNull(order) || Objects.isNull(order.getDeliveryAddress())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        PanacheQuery<Warehouse> query = warehouseRepository
                .find("select w from Warehouse w where function('distance', w.location.latitude, w.location.longitude, ?1, ?2) < 30000",
                        order.getDeliveryAddress().getLatitude(),
                        order.getDeliveryAddress().getLongitude());
        List<Warehouse> warehouses = query.list();
        if (warehouses.isEmpty()) {
            // TODO: return domain error, no warehouses available
        }
        // TODO: sort via query orderBy
        warehouses.sort(Comparator.comparingInt(w -> w.getOrders().size()));
        Warehouse selected = warehouses.get(0);
        selected.getOrders().add(order);
        warehouseRepository.flush();
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingOrder> getOrders() {
        return orderRepository.findAll().list();
    }

    @Path("/{id}")
    @PUT
    public void editOrder(@PathParam("id") String id) {

    }
}
