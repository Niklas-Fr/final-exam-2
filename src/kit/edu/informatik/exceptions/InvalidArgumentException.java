package kit.edu.informatik.exceptions;

/**
 * This exception is thrown when an invalid argument is passed to a command.
 *
 * @author uqtwh
 * @version 1.0
 */
public final class InvalidArgumentException extends Exception {

    /**
     * Constructor for the exception.
     * @param message the message of the exception
     */
    public InvalidArgumentException(String message) {
        super(message);
    }
}

