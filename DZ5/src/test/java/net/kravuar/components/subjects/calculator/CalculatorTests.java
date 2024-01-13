package net.kravuar.components.subjects.calculator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorTests {
    Calculator calculator = new CalculatorImpl();

    @ParameterizedTest(name = "factorial of {0} is {1}")
    @CsvSource({
            "0, 1",
            "3, 6",
            "4, 24",
            "7, 5040"
    })
    void factorialIsCorrect(int number, int result) {
        assertEquals(calculator.factorial(number), result);
    }

    @Test
    void throwsIllegalArgumentExceptionIfNegative() {
        assertThrows(IllegalArgumentException.class, () -> calculator.factorial(-100));
    }
}
