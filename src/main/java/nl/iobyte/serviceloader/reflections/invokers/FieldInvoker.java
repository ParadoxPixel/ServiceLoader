package nl.iobyte.serviceloader.reflections.invokers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FieldInvoker<T> {

    private final Field field;
    private final Class<T> type;

    public FieldInvoker(Field field, Class<T> type) {
        this.field = field;
        this.type = type;

        field.trySetAccessible();
    }

    /**
     * Get name of field
     * @return name
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Get type of field
     * @return type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Get field belonging to invoker
     * @return field
     */
    public Field getField() {
        return field;
    }

    /**
     * Check if field has annotation
     * @param type annotation type
     * @return has annotation
     */
    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return field.isAnnotationPresent(type);
    }

    /**
     * Get field value for class instance
     * @param obj class instance
     * @return field value
     */
    public T get(Object obj) {
        try {
            return type.cast(field.get(obj));
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Set field value for class instance
     * @param obj class instance
     * @param value field value
     */
    public void set(Object obj, T value) {
        try {
            field.set(obj, value);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cast field invoker to type
     * @param type field type
     * @return field invoker instance
     * @param <R> type
     */
    public <R> FieldInvoker<R> cast(Class<R> type) {
        if(!type.isAssignableFrom(field.getType()))
            return null;

        return InvokerWrapper.wrap(field, type);
    }

}
