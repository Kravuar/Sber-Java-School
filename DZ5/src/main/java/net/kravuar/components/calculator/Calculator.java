package net.kravuar.components.calculator;

import net.kravuar.components.cache.Cache;

public interface Calculator {

    /**
     * Calculates factorial of a number
     *
     * @throws IllegalArgumentException if {@code number} < 0
     */
    @Cache
    int factorial(int number);
}
