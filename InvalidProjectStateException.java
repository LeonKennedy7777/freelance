package exception;

/** Thrown when a project is moved to a stage that is not allowed. */
public class InvalidProjectStateException extends Exception {
    public InvalidProjectStateException(String message) {
        super(message);
    }
}
