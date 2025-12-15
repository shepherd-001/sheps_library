package com.shepherd.shepslibrary.auditing;

import com.shepherd.shepslibrary.auditing.auditMessageFormatter.AuditMessageFormatter;
import com.shepherd.shepslibrary.auditing.config.NamedThreadFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;
import org.w3c.dom.css.Counter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class AuditService implements SmartLifecycle {
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher publisher;
    private final AuditProperties auditProperties;
    private final AuditMessageFormatter messageFormatter;

    private BlockingQueue<AuditLog> queue;
    private ExecutorService auditConsumerExecutor;
    private ScheduledExecutorService auditFlushExecutor;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    // metrics
    private Counter droppedCounter;
    private Counter failedCounter;
//    private Gauge queueSizeGauge;

    @PostConstruct
    public void init(){
        this.queue = new ArrayBlockingQueue<>(auditProperties.getQueueCapacity());

        // consumer executor with named threads
        this.auditConsumerExecutor = Executors.newFixedThreadPool(
                Math.max(1, auditProperties.getConsumerThreads()),
                new NamedThreadFactory("audit-consumer", false));

        // scheduled flusher with named threads
        this.auditFlushExecutor = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("audit-flusher", false)
        );

        // Start consumer threads
        for(int i = 0; i < Math.max(1, auditProperties.getConsumerThreads()); i++) {
            auditConsumerExecutor.submit(this::cosumeLoop);
        }

        // Schedule periodic flush
        auditFlushExecutor.scheduleAtFixedRate(
                this::flushNow,
                auditProperties.getFlushIntervalMs(),
                auditProperties.getFlushIntervalMs(),
                TimeUnit.MILLISECONDS
        );
        running.set(true);
    }


    public void enqueue(AuditLog auditLog) {
        if(!auditProperties.isEnabled()) return;
        boolean offered = false;
        try{
            offered = queue.offer(auditLog, auditProperties.getEnqueueOfferTimeoutMs(), TimeUnit.MILLISECONDS);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        if(!offered) {
            if(auditProperties.isDropOnFull()){
                droppedCounterIncrement();
                // log minimal record instead
                AuditLog minimal = AuditLog.builder()
                        .actor(auditLog.getActor())
                        .endpoint(auditLog.getEndpoint())
                        .method(auditLog.getMethod())
                        .timeStamp(Instant.now())
                        .message("audit dropped (queue full)")
                        .build();

                // best-effort insert minimal (try offer immediately)
                queue.offer(minimal);
            }else {
                // fallback: try persistent write rather than blocking indefinitely
                if(auditProperties.isEnablePersistentFallback()){
                    try{
                        persistToFallback(auditLog);
                    }catch (IOException ex){
                        // last resort: block briefly to push into memory queue
                        try{
                            queue.put(auditLog);
                        }catch (InterruptedException ex1){
                            Thread.currentThread().interrupt();
                        }
                    }
                }else{
                    // block briefly
                    try{
                        queue.put(auditLog);
                    }catch (InterruptedException ex1){
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private void cosumeLoop() {
        List<AuditLog> batch = new ArrayList<>(auditProperties.getBatchSize());
        long backoffMs = auditProperties.getConsumerRetryInitialBackoff().toMillis();
        while(!Thread.currentThread().isInterrupted() && !shuttingDown.get()) {
            try{
                AuditLog first = queue.poll(500, TimeUnit.MILLISECONDS);
                if(first == null){
                    // nothing to do; loop again
                    continue;
                }
                batch.add(first);
                queue.drainTo(batch, auditProperties.getBatchSize() - 1);

                // call formatter to populate message
                batch.forEach(al -> {
                    if(al.getMessage() == null || al.getMessage().isBlank()){
                        try{
                            al.setMessage(messageFormatter.format(al));
                        }catch (Exception ex){
                            // fallback
                            al.setMessage(al.getCustomAction() != null ? al.getCustomAction() : "audit");
                        }
                    }
                });

                // save with retry/backoff
                boolean saved = false;
                int attempt = 0;
                while(!saved && attempt <= auditProperties.getMaxWriteRetries()){
                    try{
                        auditLogRepository.saveAll(batch);
                        publisher.publishEvent(new AuditBatchEvent(List.copyOf(batch)));
                        saved = true;
                        backoffMs = auditProperties.getConsumerRetryInitialBackoff().toMillis(); // reset back off
                    }catch (Exception ex){
                        failedCounterIncrement();
                        attempt++;
                        // exponential backoff caped
                        Thread.sleep(Math.min(backoffMs, auditProperties.getConsumerRetryMaxBackoff().toMillis());
                        backoffMs = Math.min(backoffMs * 2, auditProperties.getConsumerRetryMaxBackoff().toMillis());
                        if(attempt > auditProperties.getMaxWriteRetries()){
                            // final fallback: persist to filesystem for later reprocessing
                            try{
                                persistBatchToFallback(batch);
                            }catch (IOException e){
                                // log and drop (last resort)
                                // logger.error("Failed to persist audit batch to fallback, dropping", e)
                            }
                        }
                    }
                }
                batch.clear();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }catch (Throwable t){
                // logger.error("Audit consumer unexpected error", t);
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException ex){
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        // on exit flush remaining
        flushRemainingOnShutdown();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

//    @PostConstruct
//    public void start() {
//        //consumer thread
//        auditConsumerExecutor.submit(this::consume);
//
//        // periodic flush to ensure small batches don't linger
//        auditFlushExecutor.scheduleAtFixedRate(this::flushNow,
//                auditProperties.getFlushIntervalMs(),
//                auditProperties.getFlushIntervalMs(),
//                TimeUnit.MILLISECONDS);
//    }
//
//    public void enqueue(AuditLog auditLog) {
//        boolean offered = queue.offer(auditLog);
//        if(!offered) {
//            if (auditProperties.isDropOnFull()) {
//                // degrade gracefully: try to insert a minimal record
//                AuditLog minimal = AuditLog.builder()
//                        .actor(auditLog.getActor())
//                        .endpoint(auditLog.getEndpoint())
//                        .method(auditLog.getMethod())
//                        .timeStamp(Instant.now())
//                        .message("audit dropped (queue full)")
//                        .build();
//                try {
//                    queue.offer(minimal, 200, TimeUnit.MILLISECONDS);
//                } catch (InterruptedException ignored) {
//                }
//            }else{
//                // block briefly (careful) or route to filesystem
//                try{
//                    queue.put(auditLog);
//                }catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }
//    }
//
//    private void consume() {
//        List<AuditLog> batch = new ArrayList<>(auditProperties.getBatchSize());
//        while(true){
//            try{
//                AuditLog first = queue.take(); // blocks
//                batch.add(first);
//                queue.drainTo(batch, auditProperties.getBatchSize()-1);
//                auditLogRepository.saveAll(batch);
//                publisher.publishEvent(new AuditBatchEvent(batch));
//                batch.clear();
//            }catch (Throwable t){
//                // in production, log and sleep to avoid tight error loops
//                try{
//                    Thread.sleep(1000);
//                }catch (InterruptedException ignored){}
//            }
//        }
//    }
//
//    // called by scheduled flush to unblock small batches
//    private void flushNow() {
//        // queue consumer already drains; this method can wake the consumer if stuck - implement as needed.
//    }*/

}

//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            }
//        }
//        // on exit flush remaining
//        flushRemainingOnShutdown();
//    }
//
//    private void flushNow() {
//        // a no-op here triggers consumers that poll with timeout to wake frequently.
//        // But to force immediate flush we submit a small runnable to consumers that tries to drain
//        // Alternatively, we could use a synchronous queue or condition variable to notify.
//        // Here: attempt to drain a small batch and persist immediately in calling thread (best-effort)
//        List<AuditLog> batch = new ArrayList<>();
//        queue.drainTo(batch, auditProperties.getBatchSize());
//        if (!batch.isEmpty()) {
//            try {
//                auditLogRepository.saveAll(batch);
//                publisher.publishEvent(new AuditBatchEvent(List.copyOf(batch)));
//            } catch (Exception e) {
//                // if fails, push back into fallback
//                try {
//                    persistBatchToFallback(batch);
//                } catch (IOException ioe) {
//                    // swallow
//                }
//            }
//        }
//    }
//
//    private void flushRemainingOnShutdown() {
//        List<AuditLog> remaining = new ArrayList<>();
//        queue.drainTo(remaining);
//        if (!remaining.isEmpty()) {
//            try {
//                auditLogRepository.saveAll(remaining);
//                publisher.publishEvent(new AuditBatchEvent(List.copyOf(remaining)));
//            } catch (Exception e) {
//                try {
//                    persistBatchToFallback(remaining);
//                } catch (IOException ioe) {
//                    // logger.warn("Dropping audit events during shutdown", ioe);
//                }
//            }
//        }
//    }
//
//    // simple persistent fallback: append JSON lines to files; real systems should use durable queue
//    private synchronized void persistToFallback(AuditLog auditLog) throws IOException {
//        Path dir = Paths.get(auditProperties.getFallbackDirectory());
//        Files.createDirectories(dir);
//        Path f = dir.resolve("audit-" + LocalDate.now().toString() + ".log");
//        String line = encodeAuditLog(auditLog) + System.lineSeparator();
//        Files.writeString(f, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//    }
//
//    private synchronized void persistBatchToFallback(List<AuditLog> batch) throws IOException {
//        Path dir = Paths.get(auditProperties.getFallbackDirectory());
//        Files.createDirectories(dir);
//        Path f = dir.resolve("audit-" + LocalDate.now().toString() + ".log");
//        String content = batch.stream().map(this::encodeAuditLog).collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator();
//        Files.writeString(f, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//    }
//
//    private String encodeAuditLog(AuditLog a) {
//        try {
//            ObjectMapper om = new ObjectMapper();
//            return om.writeValueAsString(a);
//        } catch (JsonProcessingException e) {
//            return a.toString();
//        }
//    }
//
//    // metrics helpers
//    private void droppedCounterIncrement(){
//        if (droppedCounter != null) droppedCounter.increment();
//    }
//    private void failedCounterIncrement(){
//        if (failedCounter != null) failedCounter.increment();
//    }
//
//    // SmartLifecycle methods for graceful shutdown
//    @Override
//    public void stop() {
//        shuttingDown.set(true);
//        // attempt graceful shutdown
//        auditFlushExecutor.shutdown();
//        auditConsumerExecutor.shutdown();
//        try {
//            if (!auditConsumerExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
//                auditConsumerExecutor.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            auditConsumerExecutor.shutdownNow();
//            Thread.currentThread().interrupt();
//        }
//        running.set(false);
//    }
//
//    @Override
//    public boolean isRunning() { return running.get(); }
//
//    @Override
//    public int getPhase() { return Integer.MAX_VALUE; } // stop late
//    @Override
//    public boolean isAutoStartup() { return true; }
//    @Override
//    public void start() { /* no-op since @PostConstruct init*/ }
//}