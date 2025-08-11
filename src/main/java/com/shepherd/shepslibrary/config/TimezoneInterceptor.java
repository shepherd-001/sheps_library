package com.shepherd.shepslibrary.config;

import com.shepherd.shepslibrary.context.RequestTimezoneContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TimezoneInterceptor implements HandlerInterceptor {
    public static final String HEADER = "x-user-timezone";
    private final RequestTimezoneContext requestTimezoneContext;

    public TimezoneInterceptor(RequestTimezoneContext requestTimezoneContext) {
        this.requestTimezoneContext = requestTimezoneContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler){
        String timezone = request.getHeader(HEADER);
        requestTimezoneContext.setZoneId(timezone);
        return true;
    }
}