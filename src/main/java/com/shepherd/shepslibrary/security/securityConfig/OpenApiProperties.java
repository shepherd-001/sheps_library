package com.shepherd.shepslibrary.security.securityConfig;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "app.openapi")
@Validated
public record OpenApiProperties(
        @NotBlank String title,
        @NotBlank String version,
        @NotBlank String description,
        @NotBlank String contactName,
        @NotBlank String contactEmail,
        @NotBlank String contactUrl,
        @NotBlank String licenseName,
        @NotBlank String licenseUrl,
        @NotEmpty List<ServerDefinition> servers
) {

    public record ServerDefinition(
            @NotBlank String url,
            @NotBlank String description
    ) {
    }
}
