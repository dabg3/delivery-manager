package org.example.delivery.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/*
 * Kinda NullPointerExceptionMapper but catches missing IDs, more info there
 */

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException e) {
        ErrorResponse.ErrorMessage message
                = new ErrorResponse.ErrorMessage(e.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(message))
                .build();
    }
}
