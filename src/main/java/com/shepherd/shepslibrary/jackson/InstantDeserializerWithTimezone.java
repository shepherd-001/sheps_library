package com.shepherd.shepslibrary.jackson;

import com.shepherd.shepslibrary.context.RequestTimezoneHolder;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Slf4j
public class InstantDeserializerWithTimezone extends ValueDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext ctxt) {
        ZoneId zone = RequestTimezoneHolder.getZoneId();
        log.info("==>> Deserializing Instant – current zone context: {}", zone);
        String text = parser.getText();
        if (text == null || text.isBlank()) return null;

        OffsetDateTime odt = OffsetDateTime.parse(text);
        return odt.toInstant();
    }
}