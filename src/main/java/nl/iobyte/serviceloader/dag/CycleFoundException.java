package nl.iobyte.serviceloader.dag;

/**
 * @author KocproZ
 * Created 2018-08-14 at 11:57
 * <a href="https://github.com/KocproZ/DAG"></a>
 */
public class CycleFoundException extends RuntimeException {

    public CycleFoundException(String message) {
        super(message);
    }

}
