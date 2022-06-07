package nl.iobyte.serviceloader.objects;

import nl.iobyte.serviceloader.ServiceLoader;
import nl.iobyte.serviceloader.annotations.Inject;
import nl.iobyte.serviceloader.enums.ServiceState;
import nl.iobyte.serviceloader.interfaces.IService;
import nl.iobyte.serviceloader.reflections.ReflectionType;
import nl.iobyte.serviceloader.reflections.invokers.FieldInvoker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceContainer<T extends IService> {

    private final ReflectionType<T> type;
    private final ServiceLoader serviceLoader;
    private final List<FieldInvoker<IService>> dependencyFields = new ArrayList<>();
    private final AtomicReference<T> instance = new AtomicReference<>(null);
    private final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.NONE);

    public ServiceContainer(ReflectionType<T> type, ServiceLoader serviceLoader) {
        this.type = type;
        this.serviceLoader = serviceLoader;

        type.getGlobalFields().stream()
            .filter(field -> field.hasAnnotation(Inject.class))
            .filter(field -> IService.class.isAssignableFrom(field.getField().getType()))
            .map(field -> field.cast(IService.class))
            .forEach(dependencyFields::add);
    }

    /**
     * Get service type
     * @return type
     */
    public ReflectionType<T> getType() {
        return type;
    }

    /**
     * Get service instance
     * @return instance of service
     */
    public T getInstance() {
        return instance.get();
    }

    /**
     * Get list of dependencies
     * @return list of service types
     */
    public List<Class<? extends IService>> getDependencies() {
        List<Class<? extends IService>> list = new ArrayList<>();
        dependencyFields.forEach(field -> list.add(field.getField().getType().asSubclass(IService.class)));

        type.getConstructors().stream().reduce((a,b) -> {
            if(a.getParameterTypes().length > b.getParameterTypes().length)
                return a;

            return b;
        }).ifPresent(invoker -> {
           for(Class<?> parameter : invoker.getParameterTypes()) {
               if(!IService.class.isAssignableFrom(parameter))
                   throw new IllegalStateException("parameter type "+parameter.getSimpleName()+" is not allowed in service constructor");

               list.add(parameter.asSubclass(IService.class));
           }
        });

        return list;
    }

    /**
     * Resolve dependencies for service
     * @param service instance of service
     */
    public void resolveDependencies(T service) {
        IService value;
        for(FieldInvoker<IService> invoker : dependencyFields) {
            //Resolve service
            value = serviceLoader.resolve(
                    invoker.getField()
                           .getType()
                           .asSubclass(IService.class)
            );

            //Fail if unresolved
            if(value == null)
                throw new IllegalStateException("unable to resolve service "+invoker.getField().getType().getSimpleName());

            //Inject dependency
            invoker.set(service, value);
        }
    }

    /**
     * Initialize service
     */
    public void init() {
        if(!state.compareAndSet(ServiceState.NONE, ServiceState.INIT))
            return;

        type.getConstructors().stream().reduce((a,b) -> {
            if(a.getParameterTypes().length > b.getParameterTypes().length)
                return a;

            return b;
        }).ifPresent(invoker -> {
            T obj;
            if(invoker.hasParameters()) {
                Object[] array = new Object[invoker.getParameterTypes().length];
                for(int i = 0; i < array.length; i++) {
                    array[i] = serviceLoader.resolve(invoker.getParameterTypes()[i].asSubclass(IService.class));
                    if(array[i] == null)
                        throw new IllegalStateException("unable to resolve service "+invoker.getParameterTypes()[i].getSimpleName());
                }

                obj = invoker.newInstance(array);
            } else {
                obj = invoker.newInstance();
            }

            if(obj == null)
                throw new IllegalStateException("unable get new instance of service "+type.getName());

            instance.compareAndSet(null, obj);
        });
    }

    /**
     * Start container
     */
    public void start() {
        T obj = instance.get();
        if(obj == null)
            return;

        if(!state.compareAndSet(ServiceState.INIT, ServiceState.START))
            return;

        resolveDependencies(obj);
        obj.start();
    }

    /**
     * Stop container
     */
    public void stop() {
        T obj = instance.getAndSet(null);
        if(obj == null)
            return;

        if(!state.compareAndSet(ServiceState.START, ServiceState.STOP))
            return;

        obj.stop();
    }

}
