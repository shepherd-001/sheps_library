package com.shepherd.shepslibrary.config;

import com.shepherd.shepslibrary.context.RequestTimezoneHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TimezoneInterceptor implements HandlerInterceptor {
    public static final String HEADER = "x-user-timezone";

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String tz = request.getHeader(HEADER);
        log.info("==>> Timezone header: {}", tz);
        RequestTimezoneHolder.setZoneId(tz);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestTimezoneHolder.clear();
    }
}