package net.kravuar.components.subjects.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorTests {
    Calculator calculator = new CalculatorImpl();

    @Test
    void factorialIsCorrect() {
        assertEquals(calculator.factorial(0), 1);
        assertEquals(calculator.factorial(3), 6);
        assertEquals(calculator.factorial(4), 24);
        assertEquals(calculator.factorial(7), 5040);
    }

    @Test
    void throwsIllegalArgumentExceptionIfNegative() {
        assertThrows(IllegalArgumentException.class, () -> calculator.factorial(-100));
    }
}
