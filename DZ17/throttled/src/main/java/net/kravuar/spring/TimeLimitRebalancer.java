package net.kravuar.spring;

import java.util.Collection;

interface TimeLimitRebalancer {
    void rebalance(Collection<ManagedBucket> buckets, long bandwidth);
}
