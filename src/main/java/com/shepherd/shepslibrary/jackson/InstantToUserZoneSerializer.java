package com.shepherd.shepslibrary.jackson;

import com.shepherd.shepslibrary.context.RequestTimezoneHolder;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
public class InstantToUserZoneSerializer extends ValueSerializer<Instant> {
    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializationContext ctxt) {
        log.info("==>> Serializing Instant with timezone: {}", RequestTimezoneHolder.getZoneId());
        if (value == null) {
            gen.writeNull();
            return;
        }
        ZonedDateTime zoned = value.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(RequestTimezoneHolder.getZoneId());
        gen.writeString(zoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}