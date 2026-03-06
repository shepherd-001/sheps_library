//package com.shepherd.shepslibrary.auditing;
//
//import jakarta.servlet.AsyncEvent;
//import jakarta.servlet.AsyncListener;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.MDC;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.time.Instant;
//import java.util.Optional;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class AuditLoggingFilter extends OncePerRequestFilter {
//    private static final String TRACE_ID = "traceId";
//    private final AuditService auditService;
//    private final SensitiveDataMasker masker;
//    private final AuditProperties auditProps;
//
//    @Override
//    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
//        if (!auditProps.isEnabled()) return true;
//        String uri = request.getRequestURI();
//        return auditProps.getExcludedPaths()
//                .stream()
//                .anyMatch(uri::startsWith);
//    }
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain) throws ServletException, IOException {
//        String traceId = Optional.ofNullable(request.getHeader("X-Request-ID")).orElse(UUID.randomUUID().toString());
//        MDC.put(TRACE_ID, traceId);
//        long start = System.nanoTime();
//
//        // limited body wrapper
//        LimitedCachedBodyRequest cached = new LimitedCachedBodyRequest(request, auditProps.getMaxBodyBytes());
//        LimitedResponseWrapper responseWrapper = new LimitedResponseWrapper(response, auditProps.getMaxResponseBytes());
//
//        String actor;
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != null) {
//            actor = authentication.getName();
//        } else {
//            actor = "ANONYMOUS";
//        }
//
//        try {
//            filterChain.doFilter(cached, responseWrapper);
//            if (request.isAsyncStarted()) {
//                request.getAsyncContext().addListener(new AsyncListener() {
//                    @Override
//                    public void onComplete(AsyncEvent event) {
//                        captureAndEnqueue((HttpServletRequest) event.getSuppliedRequest(),
//                                traceId, actor, start, cached, responseWrapper);
//                    }
//
//                    @Override
//                    public void onTimeout(AsyncEvent event) {
//                    }
//
//                    @Override
//                    public void onError(AsyncEvent event) {
//                    }
//
//                    @Override
//                    public void onStartAsync(AsyncEvent event) {
//                    }
//                });
//                return;
//            }
//            captureAndEnqueue(request, traceId, actor, start, cached, responseWrapper);
//        } catch (Throwable ex) {
//            captureAndEnqueue(request, traceId, actor, start, cached, responseWrapper);
//            throw ex;
//        } finally {
//            try {
//                responseWrapper.copyBodyToResponse();
//            } catch (IOException ignored) {
//            }
//            MDC.remove(TRACE_ID);
//        }
//    }
//
//    private void captureAndEnqueue(HttpServletRequest request, String traceId,
//                                   String actor, long start, LimitedCachedBodyRequest cached,
//                                   LimitedResponseWrapper responseWrapper) {
//        long durationMs = (System.nanoTime() - start) / 1_000_000L;
//        int statusCode = responseWrapper.getStatus();
//        String ip = getClientIP(request);
//        String userAgent = Optional.ofNullable(request.getHeader("User-Agent")).orElse("");
//        if (auditProps.getExcludedPaths() == null || auditProps.getExcludedPaths().isEmpty()) {
//            // default capture policy handled elsewhere.
//        }
//        String bodySnippet = "";
//        if(auditProps.getCaptureBodyForMethods().contains(request.getMethod())) {
//            bodySnippet = masker.mask(cached.getCachedBodyAsString());
//        }
//
//        String responseSnippet = "";
//        try {
//            responseSnippet = responseWrapper.getCapturedAsString(auditProps.getMaxResponseBytes());
//        }catch (Exception ignored){}
//
//        AuditLog log = AuditLog.builder()
//                .traceId(traceId)
//                .actor(actor)
//                .actorName(null)
//                .method(request.getMethod())
//                .endpoint(request.getRequestURI())
//                .paramsJson(bodySnippet)
//                .ipAddress(ip)
//                .userAgent(userAgent)
//                .statusCode(statusCode)
//                .timeStamp(Instant.now())
//                .durationMs(durationMs)
//                .responseSnippet(responseSnippet)
//                .build();
//        auditService.enqueue(log);
//    }
//
//    private String getClientIP(HttpServletRequest request) {
//        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
//        for (String header : headers) {
//            String ip = request.getHeader(header);
//            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
//                return ip.split(",")[0].trim();
//            }
//        }
//        return Optional.ofNullable(request.getRemoteAddr()).orElse("unknown");
//    }
//}