import nl.iobyte.serviceloader.ServiceLoader;
import nl.iobyte.serviceloader.annotations.Inject;
import nl.iobyte.serviceloader.interfaces.IService;
import org.junit.Test;

public class ServiceTest {

    @Test
    public void register() {
        ServiceLoader loader = new ServiceLoader();
        loader.register(TestService.class, TestDependencyService.class);
    }

    @Test
    public void cycle() {
        ServiceLoader loader = new ServiceLoader();
        loader.register(TestService.class, TestInjectService.class, TestDependencyService.class);

        loader.init();
        loader.start();

        loader.register(HalfWayService.class);
        loader.stop();
    }

    public static class TestService implements IService {

        @Inject
        private TestDependencyService test;

        @Inject
        private TestInjectService testInjectService;

        public TestService() {
        }

    }

    public static class TestDependencyService implements IService {

        public TestDependencyService(TestInjectService service) {
        }

    }

    public static class TestInjectService implements IService {

    }

    public static class HalfWayService implements IService {

        @Inject
        private TestService service;

    }

}
