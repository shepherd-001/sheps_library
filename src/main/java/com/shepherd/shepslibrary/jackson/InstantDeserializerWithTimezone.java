package com.shepherd.shepslibrary.jackson;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.Instant;
import java.time.OffsetDateTime;

@Slf4j
public class InstantDeserializerWithTimezone extends ValueDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext ctxt) {
        String text = parser.getValueAsString();
        if (text == null || text.isBlank()) return null;

        try {
            OffsetDateTime odt = OffsetDateTime.parse(text);
            return odt.toInstant();
        } catch (Exception e) {
            log.warn("Failed to parse datetime '{}'", text, e);
            return null; // or throw JsonMappingException
        }
    }
}