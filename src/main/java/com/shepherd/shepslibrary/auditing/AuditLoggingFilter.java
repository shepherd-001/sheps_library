package com.shepherd.shepslibrary.auditing;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuditLoggingFilter extends OncePerRequestFilter {
    private final AuditService auditService;
    private final SensitiveDataMasker masker;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        long startTime = System.nanoTime();

        String actor = "ANONYMOUS";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            actor = authentication.getName();
        }

        String method = request.getMethod();
        String endpoint = request.getRequestURI();
//        String queryString = request.getQueryString();
//        String fullEndpoint = queryString != null ? endpoint + "?" + queryString : endpoint;
        String rawBody = wrappedRequest.getCachedBodyAsString();
        String maskedBody = masker.mask(rawBody);
        String ip = getClientIp(request);
        String userAGent = request.getHeader("User-Agent");

        //continue execution

        filterChain.doFilter(wrappedRequest, response);

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        int statusCode = response.getStatus();

        auditService.record(actor, method, endpoint, maskedBody, ip, userAGent, statusCode, durationMs);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator/health") || uri.startsWith("/swagger");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if(xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            // The first IP in X-Forwarded_For is the client IP
            return xForwardedForHeader.split(",")[0].trim();
        }
        //Fallback to other headers if needed (e.g., X-Real-IP for some proxies)
        String xRealIp = request.getHeader("X-Real-IP");
        if(xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        //Fallback to getRemoteAddr if no proxy headers are present
        return request.getRemoteAddr();
    }
}