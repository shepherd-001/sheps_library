package com.shepherd.shepslibrary.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.ZoneId;
//
//@Component
//@RequestScope
//@Slf4j
//public class RequestTimezoneContext {
//    private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");
//    private static final ThreadLocal<ZoneId> ZONE = new ThreadLocal<>();
//
//
//    public ZoneId getZoneId(){
//        ZoneId zone = ZONE.get();
//        return zone != null ? zone : DEFAULT_ZONE;
//    }
//
//    public void setZoneId(String zoneIdStr){
//        if(zoneIdStr == null || zoneIdStr.isBlank()){
//            ZONE.set(DEFAULT_ZONE);
//            return;
//        }
//        try{
//            ZONE.set(ZoneId.of(zoneIdStr));
//        }catch (Exception exception){
//            ZONE.set(DEFAULT_ZONE);
//        }
//    }
//
//    public void clear(){
//        ZONE.remove();
//    }
//}


@Component
@RequestScope
public class RequestTimezoneContext {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");

    private ZoneId zoneId = DEFAULT_ZONE;

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneIdStr) {
        if (zoneIdStr == null || zoneIdStr.isBlank()) {
            this.zoneId = DEFAULT_ZONE;
            return;
        }
        try {
            this.zoneId = ZoneId.of(zoneIdStr);
        } catch (Exception e) {
            this.zoneId = DEFAULT_ZONE;
        }
    }

    public void clear() {
        this.zoneId = DEFAULT_ZONE;
    }
}