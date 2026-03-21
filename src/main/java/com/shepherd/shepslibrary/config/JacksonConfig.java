package com.shepherd.shepslibrary.config;

import com.shepherd.shepslibrary.jackson.InstantDeserializerWithTimezone;
import com.shepherd.shepslibrary.jackson.InstantToUserZoneSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;

import java.time.Instant;


@Configuration
@Slf4j
public class JacksonConfig {
    @Bean
    public JsonMapperBuilderCustomizer timezoneCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("timezone-module");
            module.addSerializer(Instant.class, new InstantToUserZoneSerializer());
            module.addDeserializer(Instant.class, new InstantDeserializerWithTimezone());

             builder.addModule(module);
        };
    }
}