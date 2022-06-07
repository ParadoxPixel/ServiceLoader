package nl.iobyte.serviceloader.reflections.invokers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class InvokerWrapper {

    /**
     * Wrap constructor for class of type
     * @param constructor to wrap
     * @return class invoker instance
     * @param <T> type
     */
    public static <T> ClassInvoker<T> wrap(Constructor<T> constructor) {
        return new ClassInvoker<>(constructor);
    }

    /**
     * Wrap method with return type
     * @param method to wrap
     * @param type return type
     * @return method invoker instance
     * @param <T> type
     */
    public static <T> MethodInvoker<T> wrap(Method method, Class<T> type) {
        return new MethodInvoker<>(method, type);
    }

    /**
     * Wrap field with type
     * @param field to wrap
     * @param type value type
     * @return field invoker instance
     * @param <T> type
     */
    public static <T>FieldInvoker<T> wrap(Field field, Class<T> type) {
        return new FieldInvoker<>(field, type);
    }

}
