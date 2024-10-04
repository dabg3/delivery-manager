package org.example.delivery.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DeliveryExceptionMapper implements ExceptionMapper<DeliveryException> {

    @Override
    public Response toResponse(DeliveryException e) {
        ErrorResponse.ErrorMessage message = new ErrorResponse.ErrorMessage(e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(message))
                .build();
    }
}
