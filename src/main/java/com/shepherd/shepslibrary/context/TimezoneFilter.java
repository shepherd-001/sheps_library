package com.shepherd.shepslibrary.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZoneId;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class TimezoneFilter extends OncePerRequestFilter {
    private final RequestTimezoneHolder timezoneHolder;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String headerValue = request.getHeader(TimezoneConstants.TIMEZONE_HEADER);
        ZoneId zone = timezoneHolder.resolve(headerValue);

        log.debug("==>> Request timezone resolved: {}", zone);

        // ScopedValue where binds for entire filter chain and async tasks
        ScopedValue.where(RequestTimezoneHolder.SCOPED_ZONE, zone).run(() -> {
            try {
                filterChain.doFilter(request, response);
            } catch (IOException | ServletException e) {
                throw new RuntimeException(e); // handled outside
            }
        });
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false; // ensures async requests also pass through
    }
}