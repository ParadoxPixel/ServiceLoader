package nl.iobyte.serviceloader.reflections;

import nl.iobyte.serviceloader.reflections.invokers.ClassInvoker;
import nl.iobyte.serviceloader.reflections.invokers.InvokerWrapper;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ReflectionConstructor {

    /**
     * Get all constructors for type
     * @param type class type
     * @return list of constructors
     * @param <T> type
     */
    public static <T> List<ClassInvoker<T>> getConstructors(Class<T> type) {
        //noinspection unchecked
        return Arrays.stream(type.getConstructors())
                     .map(constructor -> (Constructor<T>) constructor)
                     .map(InvokerWrapper::wrap)
                     .toList();
    }

    /**
     * Get constructor for type without parameters
     * @param type class type
     * @return class invoker instance
     * @param <T> type
     */
    public static <T> ClassInvoker<T> getConstructor(Class<T> type) {
        try {
            return InvokerWrapper.wrap(type.getConstructor());
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Get constructor for type with parameters
     * @param type class type
     * @param parameters array of types
     * @return class invoker instance
     * @param <T> type
     */
    public static <T> ClassInvoker<T> getConstructor(Class<T> type, Class<?>... parameters) {
        try {
            return InvokerWrapper.wrap(type.getConstructor(parameters));
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Get constructor for type matching filter
     * @param type class type
     * @param filter predicate to match
     * @return class invoker instance
     * @param <T> type
     */
    public static <T> ClassInvoker<T> getConstructor(Class<T> type, Predicate<ClassInvoker<T>> filter) {
        return getConstructors(type).stream()
                                    .filter(filter)
                                    .findAny()
                                    .orElse(null);
    }

}