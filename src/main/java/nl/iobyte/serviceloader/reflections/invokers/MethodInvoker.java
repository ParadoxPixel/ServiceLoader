package nl.iobyte.serviceloader.reflections.invokers;

import java.lang.reflect.Method;

public class MethodInvoker<T> {

    private final Method method;
    private final Class<?>[] parameterTypes;
    private final Class<T> type;

    public MethodInvoker(Method method, Class<T> type) {
        this.method = method;
        this.parameterTypes = method.getParameterTypes();
        this.type = type;

        method.trySetAccessible();
    }

    /**
     * Get name of method
     * @return name
     */
    public String getName() {
        return method.getName();
    }

    /**
     * Get types of parameters
     * @return array of types
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Get type of return value
     * @return type
     */
    public Class<T> getReturnType() {
        return type;
    }

    /**
     * Invoke method and get return value
     * @param obj class instance
     * @param parameters array of parameters
     * @return return value
     */
    public T invoke(Object obj, Object... parameters) {
        try {
            return type.cast(method.invoke(obj, parameters));
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Cast method invoker to type
     * @param type return type
     * @return method invoker instance
     * @param <R> type
     */
    public <R> MethodInvoker<R> cast(Class<R> type) {
        if(!type.isAssignableFrom(method.getReturnType()))
            return null;

        return InvokerWrapper.wrap(method, type);
    }

}
