package com.shepherd.shepslibrary.security.securityConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    private static final String SECURITY_SCHEME_TYPE = "bearer";
    private static final String SECURITY_SCHEME_BEARER_FORMAT = "JWT";

    private final OpenApiProperties properties;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(buildInfo())
                .servers(buildServers())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(buildComponents());
    }

    private Info buildInfo() {
        return new Info()
                .title(properties.title())
                .version(properties.version())
                .description(properties.description())
                .contact(new Contact()
                        .name(properties.contactName())
                        .email(properties.contactEmail())
                        .url(properties.contactUrl()))
                .license(new License()
                        .name(properties.licenseName())
                        .url(properties.licenseUrl()));
    }

    private List<Server> buildServers() {
        return properties.servers().stream()
                .map(definition -> new Server()
                        .url(definition.url())
                        .description(definition.description()))
                .toList();
    }

    private Components buildComponents() {
        return new Components()
                .addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(SECURITY_SCHEME_TYPE)
                                .bearerFormat(SECURITY_SCHEME_BEARER_FORMAT));
    }
}