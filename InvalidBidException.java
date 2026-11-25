package exception;

/** Thrown when a bid amount or a bid action is not valid. */
public class InvalidBidException extends Exception {
    public InvalidBidException(String message) {
        super(message);
    }
}
