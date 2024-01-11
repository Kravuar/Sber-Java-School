package net.kravuar;

import net.kravuar.components.cache.CachedInvocationHandler;
import net.kravuar.components.metric.PerformanceCalculatorProxy;
import net.kravuar.components.reflection.ReflectionUtils;
import net.kravuar.components.subjects.calculator.Calculator;
import net.kravuar.components.subjects.calculator.CalculatorImpl;
import net.kravuar.components.subjects.reflection.HierarchyBottom;
import net.kravuar.components.subjects.reflection.StringEnum;
import net.kravuar.components.subjects.someSubject.PartialParametersSubject;
import net.kravuar.components.subjects.someSubject.PartialParametersSubjectImpl;

import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        System.out.println("ReflectionUtils: ");
        System.out.println();


        System.out.println("All methods in hierarchy: ");
        ReflectionUtils.printAllMethods(HierarchyBottom.class);


        System.out.println();
        System.out.println("All getters in hierarchy: ");
        ReflectionUtils.printAllGetters(HierarchyBottom.class);


        System.out.println();
        System.out.println("String Enum Pattern Validation Invalid Constants: ");
        System.out.println(ReflectionUtils.validateStringEnumPattern(StringEnum.class));


        System.out.println();
        System.out.println("Cache Proxy: ");
        var target = new CalculatorImpl();
        var calculator = (Calculator) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                target.getClass().getInterfaces(),
                new CachedInvocationHandler(target)
        );
        calculator.factorial(1); // Miss
        calculator.factorial(2); // Miss
        calculator.factorial(3); // Miss
        calculator.factorial(2); // Hit
        calculator.factorial(2); // Hit
        calculator.factorial(2); // Hit
        calculator.factorial(5); // Miss
        calculator.factorial(1); // Hit


        System.out.println();
        System.out.println("Partial Cache Proxy: ");
        var targetWithPartialParametersCache = new PartialParametersSubjectImpl();
        var proxyInstance = (PartialParametersSubject) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                targetWithPartialParametersCache.getClass().getInterfaces(),
                new CachedInvocationHandler(targetWithPartialParametersCache)
        );
        proxyInstance.test(2, 0, 2); // Miss
        proxyInstance.test(3, 0, 3); // Miss
        proxyInstance.test(2, 0, 3); // Miss
        proxyInstance.test(2, -1, 2); // Hit
        proxyInstance.test(2, -2, 2); // Hit
        proxyInstance.test(3, -1, 3); // Hit


        System.out.println();
        System.out.println("Metric Proxy: ");
        var metricProxy = new PerformanceCalculatorProxy(target);
        metricProxy.factorial(2);
        metricProxy.factorial(4);
        metricProxy.factorial(8);
        metricProxy.factorial(10);
        metricProxy.factorial(12);
        metricProxy.factorial(24); // Overflow
        metricProxy.factorial(32); // Overflow
    }
}
