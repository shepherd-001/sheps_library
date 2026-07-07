package com.shepherd.shepslibrary.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RequestTimezoneHolder {
    public static final ScopedValue<ZoneId> SCOPED_ZONE = ScopedValue.newInstance();

    private static final ZoneId DEFAULT = ZoneId.of(TimezoneConstants.DEFAULT_ZONE_ID);

    private static final Map<String, ZoneId> ZONE_CACHE = new ConcurrentHashMap<>();

    public ZoneId resolve(String zoneIdStr) {
        if (zoneIdStr == null || zoneIdStr.isBlank()) {
            log.debug("==>> No timezone header present — defaulting to UTC");
            return DEFAULT;
        }
        return ZONE_CACHE.computeIfAbsent(zoneIdStr, key -> {
            try {
                return ZoneId.of(key);
            } catch (Exception e) {
                log.warn("==>> Unrecognized timezone '{}' — defaulting to UTC", key);
                return DEFAULT;
            }
        });
    }

    public static ZoneId current() {
        return SCOPED_ZONE.isBound() ? SCOPED_ZONE.get() : DEFAULT;
    }
}