package com.shepherd.shepslibrary.security.securityConfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private List<String> allowedOrigins;
    private List<String> allowedMethods = List.of("GET", "POST", "DELETE", "PUT", "PATCH");
    private List<String> allowedHeaders = List.of(
            "Authorization", "Requestor-Type",
            "Origin", "X-Requested-With", "Accept",
            "Content-Type", "Cache-Control"
    );
}
