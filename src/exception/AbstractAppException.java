package exception;

public abstract class AbstractAppException extends RuntimeException {
    public AbstractAppException(String message) {
        super(message);
    }
}
