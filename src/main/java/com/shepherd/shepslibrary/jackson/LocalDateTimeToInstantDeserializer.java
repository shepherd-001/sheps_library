package com.shepherd.shepslibrary.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.shepherd.shepslibrary.context.RequestTimezoneContext;
import org.springframework.boot.jackson.JacksonComponent;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

@JacksonComponent
public class LocalDateTimeToInstantDeserializer extends JsonDeserializer<Instant> {

    private final RequestTimezoneContext context;

    public LocalDateTimeToInstantDeserializer(RequestTimezoneContext context) {
        this.context = context;
    }

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        String text = parser.getText();
        if (text == null || text.isBlank()) return null;

        LocalDateTime localDateTime = LocalDateTime.parse(text); // ISO_LOCAL_DATE_TIME
        return localDateTime.atZone(context.getZoneId()).toInstant();
    }
}