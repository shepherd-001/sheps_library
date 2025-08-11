package com.shepherd.shepslibrary.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.shepherd.shepslibrary.context.RequestTimezoneContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateTimeToInstantDeserializer extends JsonDeserializer<Instant> {
    private static final DateTimeFormatter PARSER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final RequestTimezoneContext requestTimezoneContext;

    public LocalDateTimeToInstantDeserializer(RequestTimezoneContext requestTimezoneContext) {
        this.requestTimezoneContext = requestTimezoneContext;
    }


    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String text = jsonParser.getText();
        if(text == null || text.isBlank()){
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(text, PARSER);
        return localDateTime.atZone(requestTimezoneContext.getZoneId()).toInstant();
    }
}
