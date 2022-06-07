package nl.iobyte.serviceloader;

import nl.iobyte.serviceloader.dag.DAG;
import nl.iobyte.serviceloader.enums.ServiceState;
import nl.iobyte.serviceloader.interfaces.IService;
import nl.iobyte.serviceloader.objects.ServiceContainer;
import nl.iobyte.serviceloader.reflections.ReflectionType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceLoader {

    private final DAG<Class<? extends IService>> dag = new DAG<>();
    private final Map<Class<? extends IService>, ServiceContainer<? extends IService>> containers = new ConcurrentHashMap<>();
    private final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.NONE);

    /**
     * Register service
     *
     * @param service type
     * @param <T>     extends IService
     */
    public <T extends IService> void register(Class<T> service) {
        ServiceContainer<T> container = new ServiceContainer<>(
                ReflectionType.of(service),
                this
        );

        container.getDependencies().forEach(type -> dag.addEdge(service, type));
        containers.put(service, container);

        if(state.get().hasInit()) {
            container.getDependencies()
                     .stream()
                     .map(containers::get)
                     .filter(Objects::nonNull)
                     .forEach(serviceContainer -> {
                         serviceContainer.init();
                         if(state.get().hasStart())
                             serviceContainer.start();
                     });

            container.init();
            if(state.get().hasStart())
                container.start();
        }
    }

    /**
     * Register multiple services
     * @param types array of service types
     */
    @SafeVarargs
    public final void register(Class<? extends IService>... types) {
        for(Class<? extends IService> type : types)
            register(type);
    }

    /**
     * Initialize services
     */
    public void init() {
        if(!state.compareAndSet(ServiceState.NONE, ServiceState.INIT))
            return;

        dag.update();
        dag.visitReverse(node -> {
            ServiceContainer<? extends IService> container = containers.get(node.getObject());
            if(container == null)
                throw new IllegalStateException("unknown service "+node.getObject().getSimpleName());

            container.init();
        });
    }

    /**
     * Start services
     */
    public void start() {
        if(!state.compareAndSet(ServiceState.INIT, ServiceState.START))
            return;

        dag.visitReverse(node -> {
            ServiceContainer<? extends IService> container = containers.get(node.getObject());
            container.start();
        });
    }

    /**
     * Stop services
     */
    public void stop() {
        if(!state.compareAndSet(ServiceState.START, ServiceState.STOP))
            return;

        dag.visit(node -> {
            ServiceContainer<? extends IService> container = containers.get(node.getObject());
            container.stop();
        });
    }

    /**
     * Resolve service from type
     * @param type service type
     * @return service instance
     * @param <T> extends IService
     */
    public <T extends IService> T resolve(Class<T> type) {
        return Optional.ofNullable(containers.get(type))
                       .map(container -> type.cast(container.getInstance()))
                       .orElse(null);
    }

}
