package nl.iobyte.serviceloader.reflections;

import nl.iobyte.serviceloader.reflections.invokers.ClassInvoker;
import nl.iobyte.serviceloader.reflections.invokers.FieldInvoker;
import nl.iobyte.serviceloader.reflections.invokers.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ReflectionType<T> {

    private final Class<T> type;
    private final List<FieldInvoker<Object>> ownFields, globalFields;
    private final List<MethodInvoker<?>> ownMethods, globalMethods;

    public ReflectionType(Class<T> type) {
        this.type = type;

        this.ownFields = ReflectionField.getOwnFields(type);
        this.globalFields = ReflectionField.getGlobalFields(type);

        this.ownMethods = ReflectionMethod.getOwnMethods(type);
        this.globalMethods = ReflectionMethod.getGlobalMethods(type);
    }

    /* ############
    #   General   #
    ############ */

    /**
     * Get raw type
     * @return type
     */
    public Class<T> getRawType() {
        return type;
    }

    /**
     * Get type name
     * @return name
     */
    public String getName() {
        return type.getSimpleName();
    }

    /**
     * Get package name
     * @return name
     */
    public String getPackageName() {
        return type.getPackageName();
    }

    /**
     * Get parent type of type
     * @return reflection type instance of parent
     */
    public ReflectionType<? super T> getParent() {
        return Optional.ofNullable(type.getSuperclass())
                       .map(ReflectionType::new)
                       .orElse(null);
    }

    /* #####################
    #   Constructor Data   #
    ##################### */

    /**
     * Get all constructors for type
     * @return list of constructors
     */
    public List<ClassInvoker<T>> getConstructors() {
        return ReflectionConstructor.getConstructors(type);
    }

    /**
     * Get constructor without parameters
     * @return class invoker instance
     */
    public ClassInvoker<T> getConstructor() {
        return ReflectionConstructor.getConstructor(type);
    }

    /**
     * Get constructor with parameter types
     * @return class invoker instance
     */
    public ClassInvoker<T> getConstructor(Class<?>... parameters) {
        return ReflectionConstructor.getConstructor(type, parameters);
    }

    /**
     * Get constructor matching filter
     * @param filter predicate to match
     * @return class invoker instance
     */
    public ClassInvoker<T> getConstructor(Predicate<ClassInvoker<T>> filter) {
        return ReflectionConstructor.getConstructor(type, filter);
    }

    /* ###############
    #   Field Data   #
    ############### */

    /**
     * Get all fields declared in type
     * @return list of fields
     */
    public List<FieldInvoker<Object>> getOwnFields() {
        return ownFields;
    }

    /**
     * Get all fields declared in type and parents
     * @return list of fields
     */
    public List<FieldInvoker<Object>> getGlobalFields() {
        return globalFields;
    }

    /**
     * Get own field with name
     * @param name field name
     * @return field invoker instance
     */
    public FieldInvoker<Object> getOwnFieldByName(String name) {
        return ReflectionField.getFieldByName(getOwnFields(), name);
    }

    /**
     * Get global field with name
     * @param name field name
     * @return field invoker instance
     */
    public FieldInvoker<Object> getGlobalFieldByName(String name) {
        return ReflectionField.getFieldByName(getGlobalFields(), name);
    }

    /**
     * Get own field of type
     * @param type field castable type
     * @return field invoker instance
     * @param <R> type
     */
    public <R> FieldInvoker<R> getOwnFieldByType(Class<R> type) {
        return ReflectionField.getFieldByType(getOwnFields(), type);
    }

    /**
     * Get global field of type
     * @param type field castable type
     * @return field invoker instance
     * @param <R> type
     */
    public <R> FieldInvoker<R> getGlobalFieldByType(Class<R> type) {
        return ReflectionField.getFieldByType(getGlobalFields(), type);
    }

    /* ################
    #   Method Data   #
    ################ */

    /**
     * Get all methods declared in type
     * @return list of methods
     */
    public List<MethodInvoker<?>> getOwnMethods() {
        return ownMethods;
    }

    /**
     * Get all methods declared in type and parents
     * @return list of methods
     */
    public List<MethodInvoker<?>> getGlobalMethods() {
        return globalMethods;
    }

    /**
     * Get method with name declared in type
     * @param name method name
     * @return method invoker instance
     */
    public MethodInvoker<?> getOwnMethodByName(String name) {
        return ReflectionMethod.getMethodByName(getOwnMethods(), name);
    }

    /**
     * Get global method with name
     * @param name method name
     * @return method invoker instance
     */
    public MethodInvoker<?> getGlobalMethodByName(String name) {
        return ReflectionMethod.getMethodByName(getGlobalMethods(), name);
    }

    /**
     * Get method with name declared in type
     * @param type return type
     * @return method invoker instance
     * @param <R> type
     */
    public <R> MethodInvoker<R> getOwnMethodByType(Class<R> type) {
        return ReflectionMethod.getMethodByType(getOwnMethods(), type);
    }

    /**
     * Get global method with name
     * @param type return type
     * @return method invoker instance
     * @param <R> type
     */
    public <R> MethodInvoker<R> getGlobalMethodByName(Class<R> type) {
        return ReflectionMethod.getMethodByType(getGlobalMethods(), type);
    }

    /**
     * Get own method matching filter
     * @param filter predicate to match
     * @return method invoker instance
     */
    public MethodInvoker<?> getOwnMethod(Predicate<MethodInvoker<?>> filter) {
        return ReflectionMethod.getMethod(getOwnMethods(), filter);
    }

    /**
     * Get global method matching filter
     * @param filter predicate to match
     * @return method invoker instance
     */
    public MethodInvoker<?> getGlobalMethod(Predicate<MethodInvoker<?>> filter) {
        return ReflectionMethod.getMethod(getGlobalMethods(), filter);
    }

    /**
     * Get reflection type instance from type
     * @param type class type
     * @return reflection type instance
     * @param <T> type
     */
    public static <T> ReflectionType<T> of(Class<T> type) {
        return new ReflectionType<>(type);
    }

}
