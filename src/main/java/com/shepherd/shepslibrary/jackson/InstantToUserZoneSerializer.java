package com.shepherd.shepslibrary.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.shepherd.shepslibrary.context.RequestTimezoneContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
public class InstantToUserZoneSerializer extends JsonSerializer<Instant> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private final RequestTimezoneContext requestTimezoneContext;

    public InstantToUserZoneSerializer(RequestTimezoneContext requestTimezoneContext) {
        this.requestTimezoneContext = requestTimezoneContext;
    }

    @Override
    public void serialize(Instant value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(value == null){
            jsonGenerator.writeNull();
            return;
        }
        String out = value.atZone(requestTimezoneContext.getZoneId()).format(FORMATTER);
        jsonGenerator.writeString(out);
    }
}
