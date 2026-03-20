package com.shepherd.shepslibrary.config;

import com.shepherd.shepslibrary.context.RequestTimezoneContext;
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
    private final RequestTimezoneContext requestTimezoneContext;

    public TimezoneInterceptor(RequestTimezoneContext requestTimezoneContext) {
        this.requestTimezoneContext = requestTimezoneContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler){
        String timezone = request.getHeader(HEADER);
        log.info("==>> Timezone header received: {}", timezone);
        requestTimezoneContext.setZoneId(timezone);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        requestTimezoneContext.clear();
    }
}