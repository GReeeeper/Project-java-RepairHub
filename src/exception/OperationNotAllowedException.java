package exception;

public class OperationNotAllowedException extends AbstractAppException {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}
