package com.shepherd.shepslibrary.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.shepherd.shepslibrary.context.RequestTimezoneContext;
import org.springframework.boot.jackson.JacksonComponent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@JacksonComponent
public class LocalDateTimeToUserZoneSerializer extends JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private final RequestTimezoneContext context;

    public LocalDateTimeToUserZoneSerializer(RequestTimezoneContext context) {
        this.context = context;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(value.atZone(context.getZoneId()).format(FORMATTER));
    }
}