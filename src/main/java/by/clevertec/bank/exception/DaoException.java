package by.clevertec.bank.exception;

/**
 * The `DaoException` class is a custom exception class that can be used to handle exceptions related to data access
 * objects (DAOs).
 */
public class DaoException extends Exception{
    public DaoException() {
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }
}
