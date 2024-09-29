package org.example.delivery;

import jakarta.ws.rs.*;

@Path("orders")
public class OrdersController {

    @POST
    public void addOrder(ShoppingOrder order) {

    }

    @GET
    public String getOrders() {
        return "";
    }

    @Path("/{id}")
    @PUT
    public void editOrder(@PathParam("id") String id) {

    }
}
