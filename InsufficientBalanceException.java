package exception;

/** Thrown when the client's balance is too low to release a payment. */
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
