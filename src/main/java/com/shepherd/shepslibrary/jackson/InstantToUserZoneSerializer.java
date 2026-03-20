package com.shepherd.shepslibrary.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.shepherd.shepslibrary.context.RequestTimezoneContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

//@Slf4j
//public class InstantToUserZoneSerializer extends JsonSerializer<Instant> {
//    private final RequestTimezoneContext context;
//
//    public InstantToUserZoneSerializer(RequestTimezoneContext context) {
//        this.context = context;
//    }
//
//    @Override
//    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//        log.info("==>> Serializing Instant with timezone: {}", context.getZoneId());
//        if (value == null) {
//            gen.writeNull();
//            return;
//        }
//
//        ZonedDateTime zoned = value.atZone(ZoneOffset.UTC)
//                .withZoneSameInstant(context.getZoneId());
//
//        gen.writeString(zoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//    }
//}

@Slf4j
public class InstantToUserZoneSerializer extends JsonSerializer<Instant> {

    private final Supplier<RequestTimezoneContext> contextSupplier;

    public InstantToUserZoneSerializer(Supplier<RequestTimezoneContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        RequestTimezoneContext context = contextSupplier.get(); // fetch request-scoped bean at runtime
        log.info("==>> Serializing Instant with timezone: {}", context.getZoneId());

        if (value == null) {
            gen.writeNull();
            return;
        }

        ZonedDateTime zoned = value.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(context.getZoneId());

        gen.writeString(zoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}