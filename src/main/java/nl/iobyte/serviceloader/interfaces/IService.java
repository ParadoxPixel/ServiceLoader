package nl.iobyte.serviceloader.interfaces;

public interface IService {

    /**
     * Start service
     */
    default void start() {}

    /**
     * Stop service
     */
    default void stop() {}

}
