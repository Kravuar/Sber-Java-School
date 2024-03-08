package net.kravuar.spring;

import io.github.bucket4j.*;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

class ManagedBucket {
    @Getter
    private final Bucket bucket;
    private final Refill refill;
    @Getter
    private volatile long bucketSize;
    private final AtomicLong meanConsumed = new AtomicLong();
    private long consumedCount = 0;

    ManagedBucket(long capacity, Refill refill) {
        this.bucketSize = capacity;
        this.refill = refill;
        this.bucket = Bucket.builder()
                .addLimit(Bandwidth.classic(
                        capacity,
                        refill
                ))
                .build();
    }

    public void addObservation(long consumed) {
        long newMean = ((meanConsumed.get() * consumedCount++) + consumed) / consumedCount;
        meanConsumed.set(newMean);
    }

    public void changeLimit(long newLimit) {
        bucket.replaceConfiguration(
                BucketConfiguration.builder()
                        .addLimit(Bandwidth.classic(newLimit, refill))
                        .build(),
                TokensInheritanceStrategy.PROPORTIONALLY
        );
        bucketSize = newLimit;
    }

    public long getMeanConsumed() {
        return meanConsumed.get();
    }
}
