package org.acme.openapiclient.client;

import io.vertx.core.Vertx;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.io.IOException;

/**
 * Client Request Filter that propagates headers from incoming HTTP request to outgoing REST client calls.
 * Uses Vert.x context to access headers stored by the server-side interceptor.
 */
@Provider
public class PetStoreHeaderPropagationFilter implements ClientRequestFilter {

    private static final Logger log = Logger.getLogger(PetStoreHeaderPropagationFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        Priority priority = this.getClass().getAnnotation(Priority.class);
        int priorityValue = (priority != null) ? priority.value() : Priorities.USER;
        log.infof("╔══════════════════════════════════════════════════════════");
        log.infof("║ HEADER PROPAGATION FILTER");
        log.infof("╠══════════════════════════════════════════════════════════");
        log.infof("║ Filter Priority: %d", priorityValue);
        // Try to get Vert.x context (Quarkus stores request data here)
        io.vertx.core.Context vertxContext = Vertx.currentContext();

        if (vertxContext != null) {
            log.infof("║ Found Vert.x context");

            // Get all header names stored in the context
            // Vert.x context doesn't provide a way to iterate keys, so we'll try common headers
            // and use a predefined list of headers to check
            int propagatedCount = 0;
            boolean hasAuthorization = false;
            boolean hasRequestId = false;

            // List of common headers to check for propagation
            String[] headersToPropagate = {
                "Authorization", "X-Request-ID", "X-Correlation-ID", "User-Agent",
                "Accept", "Accept-Language", "x-custom-header", "Content-Type",
                "X-Forwarded-For", "X-Real-IP", "X-Client-IP"
            };

            for (String headerName : headersToPropagate) {
                String headerValue = vertxContext.getLocal(headerName);
                if (!StringUtils.isBlank(headerValue)) {
                    requestContext.getHeaders().add(headerName, headerValue);
                    propagatedCount++;

                    if (headerName.equalsIgnoreCase("Authorization")) {
                        hasAuthorization = true;
                        log.infof("║ ✅ Propagated Authorization: %s", maskToken(headerValue));
                    } else if (headerName.equalsIgnoreCase("X-Request-ID")) {
                        hasRequestId = true;
                        log.infof("║ ✅ Propagated X-Request-ID: %s", headerValue);
                    } else {
                        log.infof("║ ✅ Propagated %s: %s", headerName, headerValue);
                    }
                }
            }

            // Generate X-Request-ID if not present
            if (!hasRequestId) {
                String generated = java.util.UUID.randomUUID().toString();
                requestContext.getHeaders().add("X-Request-ID", generated);
                log.infof("║ ✅ Generated X-Request-ID: %s", generated);
                propagatedCount++;
            }

            if (!hasAuthorization) {
                log.warnf("║ ⚠️ Authorization header not found in context");
            }

            log.infof("║ Total headers propagated: %d", propagatedCount);
        } else {
            log.warnf("║ ⚠️ No Vert.x context available");
            // Generate X-Request-ID at minimum
            String generated = java.util.UUID.randomUUID().toString();
            requestContext.getHeaders().add("X-Request-ID", generated);
            log.infof("║ ✅ Generated X-Request-ID: %s", generated);
        }

        // Add custom client headers
        requestContext.getHeaders().add("X-Client-Name", "Quarkus-OpenAPI-Client");
        requestContext.getHeaders().add("X-Client-Version", "1.0.0");

        log.infof("╚══════════════════════════════════════════════════════════");
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 4);
    }
}

