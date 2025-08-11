package com.shepherd.shepslibrary.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequestScope
@Slf4j
public class RequestTimezoneContext {
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");
//    Global cache of valid ZoneIds to reduce parsing overhead
    private static final Map<String, ZoneId> ZONE_CACHE = new ConcurrentHashMap<>();
    private ZoneId zoneId = DEFAULT_ZONE;


//    Get the current request's ZoneId (defaults to UTC)
    public ZoneId getZoneId() {
        return zoneId;
    }

//    Set ZoneId directly
    public void setZoneId(ZoneId zoneId) {
        this.zoneId = (zoneId != null) ? zoneId  : DEFAULT_ZONE;
    }

//    Parse and set ZoneId from a String, using global cache
    public void setZoneId(String  zoneIdStr) {
        if(zoneIdStr == null || zoneIdStr.isBlank()) {
            this.zoneId = DEFAULT_ZONE;
            return;
        }
        this.zoneId = ZONE_CACHE.computeIfAbsent(zoneIdStr, id ->{
            try{
                return ZoneId.of(id);
            }catch(Exception e){
                log.warn("Invalid time zone '{}', defaulting to UTC", id);
                return DEFAULT_ZONE;
            }
        });
    }
}
