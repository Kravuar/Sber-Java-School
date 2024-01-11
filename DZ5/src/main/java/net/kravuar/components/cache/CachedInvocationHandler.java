package net.kravuar.components.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CachedInvocationHandler implements InvocationHandler {
    private final Logger log = LogManager.getLogger(CachedInvocationHandler.class);
    private final Map<Map<Parameter, Object>, Object> resultByArg = new HashMap<>();
    private final Object target;

    public CachedInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.isAnnotationPresent(Cache.class))
            return invoke(method, args);

        var key = getKey(method, args);
        if (!resultByArg.containsKey(key)) {
            log.info("Cache miss on method {} with args {}.",
                    method.getName(),
                    Arrays.toString(args)
            );
            Object invoke = invoke(method, args);
            resultByArg.put(key, invoke);
        }
        else
            log.info("Cache hit on method {} with args {}.",
                    method.getName(),
                    Arrays.toString(args)
            );
        return resultByArg.get(key);
    }

    private Object invoke(Method method, Object[] args) throws Throwable {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Impossible", e);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private Map<Parameter, Object> getKey(Method method, Object[] args) {
        var parameters = method.getParameters();
        var parametersWithValues = IntStream
                .range(0, args.length)
                .boxed()
                .collect(Collectors.toMap(
                        i -> parameters[i],
                        i -> args[i]
                ));

        var key = parametersWithValues.entrySet().stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(Cache.CachedParameter.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (key.isEmpty())
            return parametersWithValues;
        return key;
    }
}
