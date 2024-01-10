package net.kravuar;

import net.kravuar.components.cache.CachedInvocationHandler;
import net.kravuar.components.cache.PartialParametersSubject;
import net.kravuar.components.cache.PartialParametersSubjectImpl;
import net.kravuar.components.calculator.Calculator;
import net.kravuar.components.calculator.CalculatorImpl;
import net.kravuar.components.reflection.HierarchyBottom;
import net.kravuar.components.reflection.ReflectionUtils;

import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        System.out.println("ReflectionUtils (enum pattern in Tests): ");
        System.out.println();

        System.out.println("All methods in hierarchy: ");
        System.out.println();
        ReflectionUtils.printAllMethods(HierarchyBottom.class);

        System.out.println();
        System.out.println("All getters in hierarchy: ");
        System.out.println();
        ReflectionUtils.printAllGetters(HierarchyBottom.class);

        System.out.println();
        System.out.println("String Enum Pattern Validation Invalid Constants: ");
        var clazz = new Object() {
            private static final String BEBE = "BEBE";
            public static final String BABA = "BABA";

            private static final String notBOBO = "BOBO";
            public static final String definitelyNotBIBI = "BIBI";
        }.getClass();
        System.out.println(ReflectionUtils.validateStringEnumPattern(clazz));

        System.out.println();
        System.out.println("Cache Proxy: ");
        System.out.println();
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
        System.out.println();
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
    }
}
