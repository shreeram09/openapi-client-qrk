package org.acme.openapiclient.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * Global exception handler for the application.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger log = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        log.errorf(exception, "An error occurred: %s", exception.getMessage());

        if (exception instanceof PetStoreException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "error", "Pet Store Error",
                            "message", exception.getMessage()
                    ))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                        "error", "Internal Server Error",
                        "message", "An unexpected error occurred"
                ))
                .build();
    }
}

