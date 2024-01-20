package net.kravuar.cache.key;

import net.kravuar.cache.addapting.ValueWrapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * Generates key as {@code String} representation of provided params array (ignoring omitted)
 * with class and method name as prefix.
 */
public class SimpleKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, ValueWrapper... params) {
        var prefix = target.getClass().getName() + '-' + method.getName() + '-';
        var postfix = Arrays.stream(params)
                .filter(Objects::nonNull)
                .reduce(new StringBuilder(),
                        (acc, e) -> acc.append(e.value()).append(';'),
                        StringBuilder::append
                ).toString();
        postfix = postfix.isBlank()
                ? "EMPTY;"
                : postfix;
        return prefix + postfix;
    }
}
