package nl.iobyte.serviceloader.enums;

public enum ServiceState {

    NONE(false, false),
    INIT(true, false),
    START(true, true),
    STOP(false, false);

    private final boolean init, start;
    ServiceState(boolean init, boolean start) {
        this.init = init;
        this.start = start;
    }

    public boolean hasInit() {
        return init;
    }

    public boolean hasStart() {
        return start;
    }

}
