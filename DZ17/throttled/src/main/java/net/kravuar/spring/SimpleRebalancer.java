package net.kravuar.spring;

import java.util.Collection;
import java.util.List;

class SimpleRebalancer implements TimeLimitRebalancer {
    private final float aggressiveness;
    private final float loadedThreshold;

    SimpleRebalancer(float aggressiveness, float loadedThreshold) {
        if (aggressiveness <= 0 || aggressiveness >= 1)
            throw new IllegalArgumentException("aggressiveness should be in (0; 0) range");
        if (loadedThreshold <= 0 || loadedThreshold > 1)
            throw new IllegalArgumentException("loadedThreshold should be in (0; 0] range");

        this.aggressiveness = aggressiveness;
        this.loadedThreshold = loadedThreshold;
    }

    SimpleRebalancer() {
        this(0.75f, 0.99f);
    }


    @Override
    public void rebalance(Collection<ManagedBucket> buckets, long bandwidth) {
        // Trim unloaded and count total taken space after cut
        long totalTaken = 0;
        for (ManagedBucket bucket : buckets) {
            long bucketFree = bucket.getBucketSize() - bucket.getMeanConsumed();
            long newSize = bucket.getBucketSize() - (long) (aggressiveness * bucketFree);
            bucket.changeLimit(newSize);
            totalTaken += newSize;
        }

        // Extend loaded
        List<ManagedBucket> loaded = buckets.stream()
                .filter(bucket -> bucket.getMeanConsumed() > bucket.getBucketSize() * loadedThreshold)
                .toList();
        int loadedCount = loaded.size();
        if (loadedCount > 0) {
            long totalFree = bandwidth - totalTaken;
            long maxDelta = totalFree / loadedCount;
            for (ManagedBucket bucket : loaded)
                bucket.changeLimit(bucket.getBucketSize() + maxDelta);
        }
    }
}
