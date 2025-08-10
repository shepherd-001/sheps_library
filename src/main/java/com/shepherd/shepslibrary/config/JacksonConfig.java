package com.shepherd.shepslibrary.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shepherd.shepslibrary.jackson.InstantToUserZoneSerializer;
import com.shepherd.shepslibrary.jackson.LocalDateTimeToInstantDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Instant;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder(
            InstantToUserZoneSerializer serializer,
            LocalDateTimeToInstantDeserializer deserializer
    ) {
        SimpleModule module = new SimpleModule()
                .addSerializer(Instant.class, serializer)
                .addDeserializer(Instant.class, deserializer);

        return new Jackson2ObjectMapperBuilder()
                .modules(new JavaTimeModule(), module);
    }
}
