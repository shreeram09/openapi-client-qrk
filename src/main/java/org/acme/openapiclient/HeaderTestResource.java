package org.acme.openapiclient;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.openapiclient.dto.PetDTO;
import org.acme.openapiclient.service.PetService;
import org.jboss.logging.Logger;

/**
 * Test endpoint for validating header propagation to REST client.
 */
@Path("/api/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeaderTestResource {

    private static final Logger log = Logger.getLogger(HeaderTestResource.class);

    @Inject
    PetService petService;

    /**
     * Test endpoint to validate header propagation.
     *
     * Usage:
     * curl -H "Authorization: Bearer test-token-123" \
     *      -H "X-Request-ID: req-456" \
     *      http://localhost:8779/api/test/headers
     */
    @GET
    @Path("/headers")
    public Response testHeaderPropagation(@Context HttpHeaders headers) {
        log.infof("╔══════════════════════════════════════════════════════════");
        log.infof("║ INCOMING REQUEST HEADERS");
        log.infof("╠══════════════════════════════════════════════════════════");

        headers.getRequestHeaders().forEach((key, values) -> {
            values.forEach(value -> {
                if (key.equalsIgnoreCase("Authorization")) {
                    log.infof("║   %s: %s", key, maskToken(value));
                } else {
                    log.infof("║   %s: %s", key, value);
                }
            });
        });

        log.infof("╚══════════════════════════════════════════════════════════");
        log.infof("Now calling PetService.getAllPets()...");

        // This will trigger the REST client call and header propagation
        try {
            petService.getAllPets(5);

            return Response.ok()
                    .entity("✅ Header propagation test completed. Check logs above for details.")
                    .build();
        } catch (Exception e) {
            log.errorf(e, "Error during header propagation test");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("❌ Error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test endpoint with custom headers.
     *
     * Usage:
     * curl -X POST http://localhost:8779/api/test/headers/custom \
     *      -H "Authorization: Bearer my-jwt-token" \
     *      -H "X-Request-ID: custom-req-789" \
     *      -H "X-Correlation-ID: corr-123" \
     *      -H "Content-Type: application/json" \
     *      -d '{"name":"TestPet","tag":"dog"}'
     */
    @POST
    @Path("/headers/custom")
    public Response testCustomHeaders(
            @Context HttpHeaders headers,
            PetDTO petDTO) {

        log.infof("╔══════════════════════════════════════════════════════════");
        log.infof("║ POST REQUEST WITH CUSTOM HEADERS");
        log.infof("╠══════════════════════════════════════════════════════════");
        log.infof("║ Pet to create: %s", petDTO);

        headers.getRequestHeaders().forEach((key, values) -> {
            log.infof("║   %s: %s", key, values);
        });

        log.infof("╚══════════════════════════════════════════════════════════");

        return Response.ok()
                .entity("✅ Custom header test completed. Check logs for propagation details.")
                .build();
    }

    /**
     * Validation endpoint that shows what headers were received.
     */
    @GET
    @Path("/headers/show")
    @Produces(MediaType.TEXT_PLAIN)
    public Response showHeaders(@Context HttpHeaders headers) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Received Headers ===\n\n");

        headers.getRequestHeaders().forEach((key, values) -> {
            values.forEach(value -> {
                if (key.equalsIgnoreCase("Authorization")) {
                    sb.append(String.format("%s: %s\n", key, maskToken(value)));
                } else {
                    sb.append(String.format("%s: %s\n", key, value));
                }
            });
        });

        sb.append("\n=== Header Propagation Status ===\n");
        sb.append(String.format("✓ Authorization: %s\n",
                headers.getRequestHeaders().containsKey("Authorization") ? "Present" : "MISSING"));
        sb.append(String.format("✓ X-Request-ID: %s\n",
                headers.getRequestHeaders().containsKey("X-Request-ID") ? "Present" : "MISSING"));
        sb.append(String.format("✓ X-Correlation-ID: %s\n",
                headers.getRequestHeaders().containsKey("X-Correlation-ID") ? "Present" : "MISSING"));

        return Response.ok(sb.toString()).build();
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 4);
    }
}

