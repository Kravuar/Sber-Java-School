package net.kravuar.components.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionUtils {
    /**
     * Prints methods of the {@code Class}, including inherited ones.
     *
     * @param clazz {@code Class} to inspect.
     */
    public static void printAllMethods(Class<?> clazz) {
        getAllMethods(clazz)
                .forEach((method) -> System.out.format(
                        "Method: %s%n",
                        method.toGenericString()
                )
        );
    }

    /**
     * Prints all getters of the {@code Class}, including inherited ones.
     *
     * @param clazz {@code Class} to inspect.
     */
    public static void printAllGetters(Class<?> clazz) {
        getAllMethods(clazz)
                .filter(ReflectionUtils::isGetter)
                .forEach((method) -> System.out.format(
                        "Getter Method: %s%n",
                        method.toGenericString()
                ));
    }

    /**
     * Validates a {@code Class} for matching the values of its {@code String} constants to their names.
     * Checks only declared constants.
     *
     * @param clazz {@code Class} to inspect.
     * @return {@code Set<Field>} of fields with invalid constants pattern.
     */
    public static Set<Field> validateStringEnumPattern(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getType().equals(String.class))
                .filter(field -> {
                    try {
                        field.setAccessible(true);
                        return !field.getName().equals(field.get(null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toSet());
    }

    /**
     * Check whether provided method is a getter.
     *
     * @return {@code boolean} result
     */
    public static boolean isGetter(Method method) {
        var methodName = method.getName();
        boolean hasPrefix = methodName.startsWith("get") || methodName.startsWith("is");
        return hasPrefix
                && Modifier.isPublic(method.getModifiers())
                && method.getReturnType() != void.class
                && method.getParameterCount() == 0;
    }

    /**
     * Check whether provided method is a setter.
     *
     * @return {@code boolean} result
     */
    public static boolean isSetter(Method method) {
        var methodName = method.getName();
        boolean hasPrefix = methodName.startsWith("set");
        return hasPrefix
                && Modifier.isPublic(method.getModifiers())
                && method.getReturnType() == void.class
                && method.getParameterCount() == 1;
    }

    private static Stream<Method> getAllMethods(Class<?> clazz) {
        if (clazz == null)
            return Stream.empty();
        return Stream.of(
                Stream.of(clazz.getDeclaredMethods()),
                getAllMethods(clazz.getSuperclass()),
                Arrays.stream(clazz.getInterfaces())
                        .flatMap(ReflectionUtils::getAllMethods)
        ).flatMap(Function.identity());
    }
}
