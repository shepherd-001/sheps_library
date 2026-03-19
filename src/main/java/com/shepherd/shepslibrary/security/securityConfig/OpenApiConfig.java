package com.shepherd.shepslibrary.security.securityConfig;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Sheps Library API",
                version = "1.0",
                description = "REST API documentation for Sheps Library system",
                contact = @Contact(
                        name = "Sheps Engineering Team",
                        email = "engineering@shepslibrary.com",
                        url = "https://shepslibrary.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
//        servers = {
//                @Server(
//                        description = "Local",
//                        url = "http://localhost:9092"
//                ),
//                @Server(
//                        description = "Development",
//                        url = "https://dev.shepslibrary.com"
//                ),
//                @Server(
//                        description = "Production",
//                        url = "https://api.shepslibrary.com"
//                )
//        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication using Bearer token",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}