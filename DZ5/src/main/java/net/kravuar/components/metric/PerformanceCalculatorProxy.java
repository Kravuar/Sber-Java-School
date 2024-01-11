package net.kravuar.components.metric;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kravuar.components.subjects.calculator.Calculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class PerformanceCalculatorProxy implements Calculator {
    private final Logger log = LogManager.getLogger(PerformanceCalculatorProxy.class);
    private final Calculator calculator;

    private static final Method factorialMethod = getMethodIfMetricAnnotated("factorial");


    @SneakyThrows
    @Override
    public int factorial(int number) {
        if (factorialMethod == null)
            return calculator.factorial(number);

        var startTime = System.nanoTime();
        var result = calculator.factorial(number);
        log.info("Method {} with number {} executed in {} nanoseconds.",
                factorialMethod.getName(),
                number,
                System.nanoTime() - startTime
        );
        return result;
    }

    @SneakyThrows
    private static Method getMethodIfMetricAnnotated(String name) {
        var method = Calculator.class.getMethod(name, int.class);
        return method.isAnnotationPresent(Metric.class)
                ? method
                : null;
    }
}
