package com.shepherd.shepslibrary.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.shepherd.shepslibrary.context.RequestTimezoneContext;
import com.shepherd.shepslibrary.jackson.InstantDeserializerWithTimezone;
import com.shepherd.shepslibrary.jackson.InstantToUserZoneSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class JacksonConfig {

//    @Bean
//    public Module timeZoneModule(RequestTimezoneContext context){
//        SimpleModule module = new SimpleModule("timezone-module");
//        module.addSerializer(Instant.class, new InstantToUserZoneSerializer(context));
//        module.addDeserializer(Instant.class, new InstantDeserializerWithTimezone(context));
//        return module;
//    }

    @Bean
    public Module timeZoneModule(ObjectProvider<RequestTimezoneContext> contextProvider) {
        SimpleModule module = new SimpleModule("timezone-module");
        module.addSerializer(Instant.class, new InstantToUserZoneSerializer(contextProvider::getIfAvailable));
        module.addDeserializer(Instant.class, new InstantDeserializerWithTimezone(contextProvider::getIfAvailable));
        return module;
    }
}
