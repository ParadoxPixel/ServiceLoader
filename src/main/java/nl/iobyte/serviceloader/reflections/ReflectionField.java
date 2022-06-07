package nl.iobyte.serviceloader.reflections;

import nl.iobyte.serviceloader.reflections.invokers.FieldInvoker;
import nl.iobyte.serviceloader.reflections.invokers.InvokerWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReflectionField {

    /**
     * Get all fields declared in class
     * @param type class
     * @return list of field invoker instances
     */
    public static List<FieldInvoker<Object>> getOwnFields(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                     .map(field -> InvokerWrapper.wrap(field, Object.class))
                     .toList();
    }

    /**
     * Get all fields from class
     * @param type type
     * @return list of field invoker instances
     */
    public static List<FieldInvoker<Object>> getGlobalFields(Class<?> type) {
        List<FieldInvoker<Object>> list = new ArrayList<>(getOwnFields(type));

        //Get field(s) from parent(s)
        Optional.ofNullable(
                type.getSuperclass()
        ).ifPresent(parent -> list.addAll(getGlobalFields(parent)));

        return list;
    }

    /**
     * Get field with name from list
     * @param fields list of fields
     * @param name field name
     * @return field invoker instance
     */
    public static FieldInvoker<Object> getFieldByName(List<FieldInvoker<Object>> fields, String name) {
        return fields.stream()
                     .filter(field -> name.equals(field.getName()))
                     .findAny()
                     .orElse(null);
    }

    /**
     * Get field of type from list
     * @param fields list of fields
     * @param type field castable type
     * @return field invoker instance
     * @param <R> type
     */
    public static <R> FieldInvoker<R> getFieldByType(List<FieldInvoker<Object>> fields, Class<R> type) {
        return fields.stream()
                     .filter(field -> type.isAssignableFrom(field.getType()))
                     .findAny()
                     .map(field -> field.cast(type))
                     .orElse(null);
    }

}