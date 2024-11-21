package exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String massage, Exception exception) {
        super(massage, exception);
    }
}
