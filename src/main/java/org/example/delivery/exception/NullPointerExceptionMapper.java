package org.example.delivery.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/*
 * This replaces a lot of `if`(s) validating the input dto at the controller level.
 * Of course all NullPointerException(s) that don't get caught trigger this mapper, not optimal.
 * A better way to do this is by using Bean Validation https://beanvalidation.org/3.0/
 */

@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException> {

    @Override
    public Response toResponse(NullPointerException e) {
        ErrorResponse.ErrorMessage message =
                new ErrorResponse.ErrorMessage("Missing request body or malformed");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(message))
                .build();
    }
}
