package com.shepherd.shepslibrary.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.shepherd.shepslibrary.context.RequestTimezoneContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.function.Supplier;


//@Slf4j
//public class InstantDeserializerWithTimezone extends JsonDeserializer<Instant> {
//
//    private final RequestTimezoneContext context;
//
//    public InstantDeserializerWithTimezone(RequestTimezoneContext context) {
//        this.context = context;
//    }
//
//    @Override
//    public Instant deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
//        log.info("==>> Deserializing Instant with timezone: {}", context.getZoneId());
//
//        String text = parser.getText();
//        if (text == null || text.isBlank()) return null;
//
//        // ISO_OFFSET_DATE_TIME
//        OffsetDateTime odt = OffsetDateTime.parse(text);
//        return odt.toInstant();
//    }
//}



@Slf4j
public class InstantDeserializerWithTimezone extends JsonDeserializer<Instant> {
    private final Supplier<RequestTimezoneContext> contextSupplier;

    public InstantDeserializerWithTimezone(Supplier<RequestTimezoneContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
    }

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        RequestTimezoneContext context = contextSupplier.get(); // fetch request-scoped bean at runtime

        log.info("==>> Deserializing Instant with timezone: {}", context.getZoneId());
        String text = parser.getText();
        if (text == null || text.isBlank()) return null;

        // ISO_OFFSET_DATE_TIME
        OffsetDateTime odt = OffsetDateTime.parse(text);
        return odt.toInstant();
    }
}