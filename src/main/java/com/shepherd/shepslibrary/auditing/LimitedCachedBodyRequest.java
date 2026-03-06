//package com.shepherd.shepslibrary.auditing;
//
//import jakarta.servlet.ReadListener;
//import jakarta.servlet.ServletInputStream;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//
//import java.io.*;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.util.Optional;
//import java.util.zip.GZIPInputStream;
//
//public class LimitedCachedBodyRequest extends HttpServletRequestWrapper {
//    private final byte[] cachedBody;
//    private static final Charset UTF8 = StandardCharsets.UTF_8;
//
//    public LimitedCachedBodyRequest(HttpServletRequest request, int maxBytes) throws IOException {
//        super(request);
//
//        // Determine if we should skip caching based on content type
//        String ct = Optional.ofNullable(request.getContentType()).orElse("").toLowerCase();
//        boolean shouldSkipCaching = ct.startsWith("multipart/") ||
//                ct.startsWith("application/octet-stream") ||
//                ct.startsWith("image/") ||
//                ct.startsWith("video/") ||
//                ct.startsWith("audio/");
//
//        if (shouldSkipCaching) {
//            this.cachedBody = new byte[0];
//            return;
//        }
//
//        byte[] tempCachedBody;
//
//        try {
//            boolean gzipped = "gzip".equalsIgnoreCase(request.getHeader("Content-Encoding"));
//
//            try (InputStream in = gzipped ?
//                    new GZIPInputStream(request.getInputStream()) :
//                    request.getInputStream();
//                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//
//                byte[] buffer = new byte[4096];
//                int total = 0;
//                int read;
//
//                while ((read = in.read(buffer)) != -1 && total < maxBytes) {
//                    int toWrite = Math.min(read, maxBytes - total);
//                    baos.write(buffer, 0, toWrite);
//                    total += toWrite;
//                }
//
//                tempCachedBody = baos.toByteArray();
//            }
//        } catch (IOException e) {
//            // Log the exception if needed, but continue with empty body
//            // logger.warn("Failed to cache request body", e);
//            tempCachedBody = new byte[0];
//        }
//
//        // Single assignment to final field
//        this.cachedBody = tempCachedBody;
//    }
//
//    @Override
//    public ServletInputStream getInputStream() {
//        ByteArrayInputStream bais = new ByteArrayInputStream(this.cachedBody);
//        return new ServletInputStream() {
//            @Override public int read() { return bais.read(); }
//            @Override public boolean isFinished() { return bais.available() == 0; }
//            @Override public boolean isReady() { return true; }
//            @Override public void setReadListener(ReadListener readListener) {}
//        };
//    }
//
//    @Override
//    public BufferedReader getReader() {
//        return new BufferedReader(new InputStreamReader(getInputStream(), UTF8));
//    }
//
//    public String getCachedBodyAsString() {
//        return cachedBody.length == 0 ? "" : new String(cachedBody, UTF8);
//    }
//}