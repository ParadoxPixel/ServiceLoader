package nl.iobyte.serviceloader.reflections;

import nl.iobyte.serviceloader.reflections.invokers.InvokerWrapper;
import nl.iobyte.serviceloader.reflections.invokers.MethodInvoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReflectionMethod {

    /**
     * Get all methods declared in class
     * @param type class
     * @return list of methods
     */
    public static List<MethodInvoker<?>> getOwnMethods(Class<?> type) {
        return new ArrayList<>(
                Arrays.stream(type.getDeclaredMethods())
                                     .map(method -> InvokerWrapper.wrap(method, Object.class))
                                     .toList()
        );
    }

    /**
     * Get all methods from class
     * @param type type
     * @return list of methods
     */
    public static List<MethodInvoker<?>> getGlobalMethods(Class<?> type) {
        List<MethodInvoker<?>> list = new ArrayList<>(getOwnMethods(type));

        //Get method(s) from parent(s)
        Optional.ofNullable(
                type.getSuperclass()
        ).ifPresent(parent -> list.addAll(getGlobalMethods(parent)));

        return list;
    }

    /**
     * Get method from list with name
     * @param methods list of methods
     * @param name name
     * @return method invoker instance
     */
    public static MethodInvoker<?> getMethodByName(List<MethodInvoker<?>> methods, String name) {
        return methods.stream()
                      .filter(method -> method.getName().equals(name))
                      .findAny()
                      .orElse(null);
    }

    /**
     * Get method from list with return type
     * @param methods list of methods
     * @param type return type
     * @return method invoker instance
     * @param <T> type
     */
    public static <T> MethodInvoker<T> getMethodByType(List<MethodInvoker<?>> methods, Class<T> type) {
        return methods.stream()
                      .filter(method -> type.isAssignableFrom(method.getReturnType()))
                      .findAny()
                      .map(method -> method.cast(type))
                      .orElse(null);
    }

    /**
     * Get method from list with return type
     * @param methods list of methods
     * @param parameters list of types
     * @return method invoker instance
     */
    public static MethodInvoker<?> getMethodByParameterTypes(List<MethodInvoker<?>> methods, Class<?>... parameters) {
        return methods.stream()
                      .filter(method -> {
                         Class<?>[] array = method.getParameterTypes();
                         if(array.length != parameters.length)
                             return false;

                         for(int i = 0; i < parameters.length; i++)
                             if(!array[i].isAssignableFrom(parameters[i]))
                                 return false;

                         return true;
                      })
                      .findAny()
                      .orElse(null);
    }

    /**
     * Get method from list matching filter
     * @param methods list of methods
     * @param filter predicate to match
     * @return method invoker instance
     */
    public static MethodInvoker<?> getMethod(List<MethodInvoker<?>> methods, Predicate<MethodInvoker<?>> filter) {
        return methods.stream()
                      .filter(filter)
                      .findAny()
                      .orElse(null);
    }

}