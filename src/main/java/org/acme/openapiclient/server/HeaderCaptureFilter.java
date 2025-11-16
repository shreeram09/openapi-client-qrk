package org.acme.openapiclient.server;

import io.vertx.core.Vertx;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;

/**
 * Server-side request filter that captures headers from incoming requests
 * and stores them in the Vert.x context for later use by REST client filters.
 */
@Provider
public class HeaderCaptureFilter implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(HeaderCaptureFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Priority priority = this.getClass().getAnnotation(Priority.class);
        int priorityValue = (priority != null) ? priority.value() : Priorities.USER;
        // Get Vert.x context
        io.vertx.core.Context vertxContext = Vertx.currentContext();
        log.infof("╔══════════════════════════════════════════════════════════");
        log.infof("║ INCOMMING REST SERVER REQUEST");
        log.infof("╠══════════════════════════════════════════════════════════");
        log.infof("║ Method: %s", requestContext.getMethod());
        log.infof("║ URI: %s", requestContext.getUriInfo().getRequestUri());
        log.infof("║ Filter Priority: %d", priorityValue);
        log.infof("╠══════════════════════════════════════════════════════════");

        if (vertxContext != null) {
            log.infof("║ HEADERS:");
            // Capture ALL headers from the incoming request
            int capturedCount = 0;

            for (String headerName : requestContext.getHeaders().keySet()) {
                // Skip standard HTTP headers that shouldn't be propagated
                if (shouldPropagateHeader(headerName)) {
                    String headerValue = requestContext.getHeaderString(headerName);
                    if (headerValue != null) {
                        vertxContext.putLocal(headerName, headerValue);
                        capturedCount++;
                        // Log headers
                        log.infof("║   %s: %s", headerName, headerName.equalsIgnoreCase("Authorization") ? maskToken(headerValue) : headerValue);
                    }
                }
            }
            log.infof("╠══════════════════════════════════════════════════════════");
            log.debugf("║    Captured: %d ", capturedCount);
        } else {
            log.warnf("║    No Vert.x context available to store headers");
        }
        log.infof("╚══════════════════════════════════════════════════════════");
    }

    /**
     * Determines if a header should be propagated to downstream services.
     * Excludes standard HTTP headers that are connection-specific.
     */
    private boolean shouldPropagateHeader(String headerName) {
        String lowerName = headerName.toLowerCase();

        // Exclude connection-specific and hop-by-hop headers
        return !lowerName.equals("host") &&
               !lowerName.equals("connection") &&
               !lowerName.equals("content-length") &&
               !lowerName.equals("transfer-encoding") &&
               !lowerName.equals("upgrade") &&
               !lowerName.equals("accept-encoding") &&
               !lowerName.equals("te") &&
               !lowerName.equals("trailer") &&
               !lowerName.equals("proxy-authorization") &&
               !lowerName.equals("proxy-authenticate") &&
               !lowerName.equals("keep-alive");
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 4);
    }
}

