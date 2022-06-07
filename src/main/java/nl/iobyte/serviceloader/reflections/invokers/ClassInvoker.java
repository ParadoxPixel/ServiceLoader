package nl.iobyte.serviceloader.reflections.invokers;

import java.lang.reflect.Constructor;

public class ClassInvoker<T> {

    private final Constructor<T> constructor;

    public ClassInvoker(Constructor<T> constructor) {
        this.constructor = constructor;

        constructor.trySetAccessible();
    }

    /**
     * Get type of class
     * @return type
     */
    public Class<T> getType() {
        return constructor.getDeclaringClass();
    }

    /**
     * Get constructor parameter types
     * @return array of types
     */
    public Class<?>[] getParameterTypes() {
        return constructor.getParameterTypes();
    }

    /**
     * Check if constructor has parameters
     * @return has parameters
     */
    public boolean hasParameters() {
        return constructor.getParameterCount() != 0;
    }

    /**
     * Get new instance of class without parameters
     * @return class instance
     */
    public T newInstance() {
        try {
            return constructor.newInstance();
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Get new instance of class with parameters
     * @param parameters array of parameters
     * @return class instance
     */
    public T newInstance(Object... parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch(Exception e) {
            return null;
        }
    }

}
