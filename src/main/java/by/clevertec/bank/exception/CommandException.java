package by.clevertec.bank.exception;

import by.clevertec.bank.model.dto.CustomError;

/**
 * The CommandException class is a custom exception that can be thrown to handle errors in a command execution, with an
 * optional HTTP code.
 */
public class CommandException extends Exception {
    private int httpCode = CustomError.INTERNAL;

    public CommandException() {
        super();
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, int code) {
        super(message);
        this.httpCode = code;
    }

    public CommandException(String message, int code, Throwable cause) {
        super(message, cause);
        this.httpCode = code;
    }

    public CommandException(Throwable cause) {
        super(cause);
        if (cause instanceof ServiceException serviceException){
            this.httpCode = serviceException.getHttpCode();
        }
    }

    public int getHttpCode() {
        return httpCode;
    }
}
