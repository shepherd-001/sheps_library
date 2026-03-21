package com.shepherd.shepslibrary.context;

import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class RequestTimezoneHolder {

    private static final ThreadLocal<ZoneId> ZONE = new ThreadLocal<>();

    private static final ZoneId DEFAULT = ZoneId.of("UTC");

    public static void setZoneId(String zoneIdStr) {
        if (zoneIdStr == null || zoneIdStr.isBlank()) {
            ZONE.set(DEFAULT);
            return;
        }
        try {
            ZONE.set(ZoneId.of(zoneIdStr));
        } catch (Exception e) {
            ZONE.set(DEFAULT);
        }
    }

    public static ZoneId getZoneId() {
        ZoneId z = ZONE.get();
        return z != null ? z : DEFAULT;
    }

    public static void clear() {
        ZONE.remove();
    }
}