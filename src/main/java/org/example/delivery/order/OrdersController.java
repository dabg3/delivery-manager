package org.example.delivery.order;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.delivery.exception.DeliveryNotAvailableException;
import org.example.delivery.location.GeoPoint;
import org.example.delivery.warehouse.Delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("orders")
class OrdersController {

    private final Delivery delivery;
    private final OrderService orderService;

    @Inject
    public OrdersController(Delivery delivery,
                            OrderService orderService) {
        this.delivery = delivery;
        this.orderService = orderService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrder(ShoppingOrderDTO orderDTO) throws DeliveryNotAvailableException {
        delivery.register(DTOToEntity(orderDTO));
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShoppingOrderDTO> getOrders(@DefaultValue("id") @QueryParam("sort") String sortField,
                                            @DefaultValue("ascending") @QueryParam("order") String order,
                                            @QueryParam("state") Optional<String> stateFilter) {
        return orderService.retrieveOrders(sortField, order, stateFilter).stream()
                .map(OrdersController::entityToDTO)
                .toList();
    }


    @Path("/{id}")
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editOrder(@PathParam("id") Long id, ShoppingOrderDTO orderDTO) {
        GeoPoint newAddress = orderDTO.getDeliveryAddress();
        orderService.updateAddress(id, newAddress);
        return Response.ok().build();
    }

    /*
     * TODO: seamless entity<->DTO conversion
     * https://modelmapper.org/
     * https://commons.apache.org/proper/commons-beanutils/
     * https://mapstruct.org/
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
