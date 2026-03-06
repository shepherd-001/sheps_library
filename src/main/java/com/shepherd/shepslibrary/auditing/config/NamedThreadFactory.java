//package com.shepherd.shepslibrary.auditing.config;
//
//import lombok.RequiredArgsConstructor;
//
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@RequiredArgsConstructor
//public class NamedThreadFactory implements ThreadFactory {
//    private final String basename;
//    private final boolean daemon;
//    private final AtomicInteger counter = new AtomicInteger();
//
//    @Override
//    public Thread newThread(Runnable r) {
//        Thread t = new Thread(r, basename + "-" + counter.getAndIncrement());
//        t.setDaemon(daemon);
//        if(t.getPriority() != Thread.NORM_PRIORITY) {
//            t.setPriority(Thread.NORM_PRIORITY);
//        }
//        return t;
//    }
//}