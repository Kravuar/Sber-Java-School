package net.kravuar.components.subjects.calculator;

import net.kravuar.components.cache.Cache;
import net.kravuar.components.metric.Metric;

public interface Calculator {

    /**
     * Calculates factorial of a number
     *
     * @throws IllegalArgumentException if {@code number} < 0
     */
    @Cache
    @Metric
    int factorial(int number);
}
