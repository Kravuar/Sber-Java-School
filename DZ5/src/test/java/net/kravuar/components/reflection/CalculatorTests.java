package net.kravuar.components.reflection;

import net.kravuar.components.calculator.Calculator;
import net.kravuar.components.calculator.CalculatorImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalculatorTests {
    Calculator calculator;

    @BeforeAll
    void init() {
        this.calculator = new CalculatorImpl();
    }

    @Test
    void factorialIsCorrect() {
        assertEquals(calculator.factorial(3), 6);
        assertEquals(calculator.factorial(4), 24);
        assertEquals(calculator.factorial(7), 5040);
    }

    @Test
    void factorialOfZeroIsOne() {
        assertEquals(calculator.factorial(0), 1);
    }

    @Test
    void throwsIllegalArgumentExceptionIfNegative() {
        assertThrows(IllegalArgumentException.class, () -> calculator.factorial(-100));
    }
}