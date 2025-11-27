package org.acme.openapiclient.client;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;

/**
 * Filter that logs all outgoing REST client requests.
 * Validates that headers are properly set before sending to external API.
 */
@Provider
public class PetStoreClientLoggingFilter implements ClientRequestFilter {

    private static final Logger log = Logger.getLogger(PetStoreClientLoggingFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        log.infof("╔══════════════════════════════════════════════════════════");
        log.infof("║ OUTGOING REST CLIENT REQUEST");
        log.infof("╠══════════════════════════════════════════════════════════");
        log.infof("║ Method: %s", requestContext.getMethod());
        log.infof("║ URI: %s", requestContext.getUri());
        log.infof("╠══════════════════════════════════════════════════════════");
        log.infof("║ HEADERS:");

        requestContext.getHeaders().forEach((key, values) -> {
            values.forEach(value -> {
                if (key.equalsIgnoreCase("Authorization")) {
                    log.infof("║   %s: %s", key, maskToken(String.valueOf(value)));
                } else {
                    log.infof("║   %s: %s", key, value);
                }
            });
        });

        log.infof("╚══════════════════════════════════════════════════════════");

        // Validate critical headers
        if (!requestContext.getHeaders().containsKey("Authorization")) {
            log.warnf("⚠️ WARNING: Authorization header is MISSING!");
        } else {
            log.infof("✅ Authorization header is present");
        }

        if (!requestContext.getHeaders().containsKey("X-Request-ID")) {
            log.warnf("⚠️ WARNING: X-Request-ID header is MISSING!");
        } else {
            log.infof("✅ X-Request-ID header is present: %s",
                    requestContext.getHeaders().getFirst("X-Request-ID"));
        }

        // Check for x-custom-header
        if (requestContext.getHeaders().containsKey("x-custom-header")) {
            log.infof("✅ x-custom-header is present: %s",
                    requestContext.getHeaders().getFirst("x-custom-header"));
        }
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 4);
    }
}

