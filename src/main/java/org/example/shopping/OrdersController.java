package org.example.shopping;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.warehouse.Delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("orders")
class OrdersController {

    private final Delivery delivery;
    private final OrderRepository orderRepository;

    @Inject
    public OrdersController(Delivery delivery,
                            OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.delivery = delivery;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrder(ShoppingOrderDTO orderDTO) {
        if (Objects.isNull(orderDTO) || Objects.isNull(orderDTO.getDeliveryAddress())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        delivery.register(DTOToEntity(orderDTO));
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingOrderDTO> getOrders() {
        // TODO filtering, ordering
        PanacheQuery<ShoppingOrderEntity> query = orderRepository.findAll();
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
        ShoppingOrderEntity order = orderRepository.findById(id);
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        orderRepository.flush();
        return Response.ok().build();
    }

    /*
     * TODO: seamless entity<->DTO conversion
     * https://modelmapper.org/
     * https://commons.apache.org/proper/commons-beanutils/
     */

    private static ShoppingOrderEntity DTOToEntity(ShoppingOrderDTO dto) {
        ShoppingOrderEntity order = new ShoppingOrderEntity();
        List<ShoppingOrderEntity.ProductDetail> products = new ArrayList<>();
        order.setDeliveryAddress(dto.getDeliveryAddress());
        for (ShoppingOrderDTO.ProductDetailDTO pDTO : dto.getProducts()) {
            ShoppingOrderEntity.ProductDetail p = new ShoppingOrderEntity.ProductDetail();
            p.setProductId(pDTO.getProductId());
            p.setQuantity(pDTO.getQuantity());
            products.add(p);
        }
        order.setProducts(products);
        return order;
    }

    private static ShoppingOrderDTO entityToDTO(ShoppingOrderEntity order) {
        ShoppingOrderDTO dto = new ShoppingOrderDTO();
        List<ShoppingOrderDTO.ProductDetailDTO> productDTOs = new ArrayList<>();
        dto.setId(order.getId());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setState(order.getState().toString());
        dto.setSubmissionDate(order.getSubmissionDate());
        for (ShoppingOrderEntity.ProductDetail p : order.getProducts()) {
            ShoppingOrderDTO.ProductDetailDTO pDTO = new ShoppingOrderDTO.ProductDetailDTO();
            pDTO.setProductId(p.getProductId());
            pDTO.setQuantity(p.getQuantity());
            productDTOs.add(pDTO);
        }
        dto.setProducts(productDTOs);
        return dto;
    }
}
