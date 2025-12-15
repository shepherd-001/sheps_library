package com.shepherd.shepslibrary.auditing;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class LimitedResponseWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final ServletOutputStream servletOutputStream;
    private PrintWriter writer;
    private final int maxBytes;
    private final AtomicBoolean copied = new AtomicBoolean(false);
    @Getter
    private int status = 200;

    public LimitedResponseWrapper(HttpServletResponse response, int maxBytes) {
        super(response);
        this.maxBytes = Math.max(1024, maxBytes);
        this.servletOutputStream = new ServletOutputStream() {
            @Override public boolean isReady() {return true;}
            @Override public void setWriteListener(WriteListener writeListener) {}
            @Override public void write(int b) throws IOException {
                if(baos.size() < LimitedResponseWrapper.this.maxBytes)
                    baos.write(b);
            }
        };
    }

    @Override
    public ServletOutputStream getOutputStream(){
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter(){
        if(writer == null){
            writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
        }
        return writer;
    }

    @Override
    public void setStatus(int sc){
        super.setStatus(sc);
        this.status = sc;
    }

    @Override
    public void sendError(int sc)throws IOException{
        super.sendError(sc);
        this.status = sc;
    }

    @Override
    public void sendError(int sc, String msg)throws IOException{
        super.sendError(sc, msg);
        this.status = sc;
    }

    @Override
    public void sendRedirect(String location)throws IOException{
        super.sendRedirect(location);
        this.status = 302;
    }

    public String getCapturedAsString(int maxBytes) {
        if(writer != null) writer.flush();
        byte[] arr = baos.toByteArray();
        int len = Math.min(arr.length, Math.max(0, maxBytes));
        return new String(Arrays.copyOf(arr, len), StandardCharsets.UTF_8);
    }

    // ensure we copy captured bytes back to the real response
    public void copyBodyToResponse() throws IOException {
        if(copied.getAndSet(true)) return;
        HttpServletResponse response = (HttpServletResponse) getResponse();
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }
}