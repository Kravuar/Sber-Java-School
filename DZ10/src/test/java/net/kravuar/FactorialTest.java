package net.kravuar;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactorialTest {
    @Test
    void whenInputIsZero_thenResultIsOne() {
        // given
        int input = 0;
        BigInteger expected = BigInteger.valueOf(1);

        // when
        BigInteger result = Factorial.factorial(input);

        // then
        assertEquals(expected, result);
    }

    @Test
    void whenInputIsPositive_thenCalculatesFactorialCorrectly() {
        // given
        int input = 5;
        BigInteger expected = BigInteger.valueOf(120);

        // when
        BigInteger result = Factorial.factorial(input);

        // then
        assertEquals(expected, result);
    }

    @Test
    void whenInputIsBig_thenCalculatesFactorialCorrectly() {
        // given
        int input = 30;
        BigInteger expected = new BigInteger("265252859812191058636308480000000");

        // when
        BigInteger result = Factorial.factorial(input);

        // then
        assertEquals(expected, result);
    }

    @Test
    void whenInputIsNegative_thenThrowsException() {
        // given
        int input = -5;

        // then
        assertThrows(IllegalArgumentException.class, () -> Factorial.factorial(input));
    }
}