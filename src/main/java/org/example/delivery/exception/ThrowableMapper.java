package org.example.delivery.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ThrowableMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        ErrorResponse.ErrorMessage message =
                new ErrorResponse.ErrorMessage("An unknown error happened");
        return Response.serverError()
                .entity(new ErrorResponse(message))
                .build();
    }
}
