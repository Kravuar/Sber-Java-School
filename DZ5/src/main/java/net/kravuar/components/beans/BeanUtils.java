package net.kravuar.components.beans;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanUtils {
    /**
     * Scans object "from" for all getters. If object "to"
     * contains correspondent setter, it will invoke it
     * to set property value for "to" which equals to the property
     * of "from".
     * <p/>
     * The type in setter should be compatible to the value returned
     * by getter (if not, no invocation performed).
     * Compatible means that parameter type in setter should
     * be the same or be superclass of the return type of the getter.
     * <p/>
     * The method takes care only about public methods.
     *
     * @param to   Object which properties will be set.
     * @param from Object which properties will be used to get values.
     */
    public static void assign(Object to, Object from) {
        var toSetters = Arrays.stream(to.getClass().getMethods())
                .filter(method -> method.getName().startsWith("set"))
                .collect(Collectors.toMap(
                        method -> method.getName().replace("set", ""),
                        Function.identity()
                ));
        Arrays.stream(from.getClass().getMethods())
                .filter(method -> method.getName().startsWith("get") || method.getName().toLowerCase().startsWith("is"))
                .forEach(getter -> {
                    var getterType = getter.getReturnType();
                    var propertyName = getter.getName().startsWith("get")
                            ? getter.getName().replace("get", "")
                            : getter.getName().replace("is", "");

                    var setter = toSetters.get(propertyName);
                    if (setter == null)
                        return;

                    var setterParameters = setter.getParameterTypes();
                    if (setterParameters.length == 0 || !setterParameters[0].isAssignableFrom(getterType))
                        return;

                    try {
                        setter.invoke(to, getter.invoke(from));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Should never Happen", e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException("Assign exception", e);
                    }
                });
    }
}