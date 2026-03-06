//package com.shepherd.shepslibrary.auditing.config;
//
//import com.shepherd.shepslibrary.auditing.AuditProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
//@Configuration
//public class AuditConfiguration {
//    @Bean(destroyMethod = "shutdown")
//    public ExecutorService auditConsumerExecutor(AuditProperties props){
//        return Executors.newFixedThreadPool(
//                Math.max(1, props.getConsumerThreads()),
//                new NamedThreadFactory("audit-consumer", true)
//        );
//    }
//
//    @Bean(destroyMethod = "shutdown")
//    public ScheduledExecutorService auditFlushExecutor(){
//        return Executors.newSingleThreadScheduledExecutor(
//                new NamedThreadFactory("audit-flush", true)
//        );
//    }
//}