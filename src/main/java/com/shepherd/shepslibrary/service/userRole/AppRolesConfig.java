package com.shepherd.shepslibrary.service.userRole;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.seed")
@Getter
@Setter
public class AppRolesConfig {
    private List<String> roles;
}
