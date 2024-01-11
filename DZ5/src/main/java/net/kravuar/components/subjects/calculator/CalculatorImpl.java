package net.kravuar.components.subjects.calculator;

public class CalculatorImpl implements Calculator {

    @Override
    public int factorial(int number) {
        if (number < 0)
            throw new IllegalArgumentException("Number cannot be negative.");
        var factorial = 1;
        for (int i = 2; i <= number; ++i)
            factorial = factorial * i;
        return factorial;
    }
}
