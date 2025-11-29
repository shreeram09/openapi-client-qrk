package org.acme.openapiclient.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

@ApplicationPath("/")
@OpenAPIDefinition(
    info = @Info(
        title = "Swagger Petstore Client",
        version = "1.0.0",
        description = "A sample Pet Store Client based on the OpenAPI 3.0 specification",
        license = @License(
            name = "MIT"
        )
    ),
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecuritySchemes({
    @SecurityScheme(
        securitySchemeName = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Bearer token authentication. Example: 'Bearer <token>'"
    )
})
public class OpenApiConfig extends Application {
}
