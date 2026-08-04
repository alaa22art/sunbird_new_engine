package com.certacure.lis.interfaces.middleware.parser.hl7;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class UniqueSequenceGenerator {
    private static final AtomicLong lastTime = new AtomicLong(0);
    private static final AtomicLong counter = new AtomicLong(0);
    
    public static synchronized String generateHighResSequence() {
        long now = Instant.now().toEpochMilli();
        long last = lastTime.get();
        
        if (now == last) {
            counter.incrementAndGet();
        } else {
            counter.set(0);
            lastTime.set(now);
        }
        
        return Long.toString((now * 100) + counter.get());
    }
}