package by.clevertec.bank.exception;

import by.clevertec.bank.model.dto.CustomError;

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
    }

    public int getHttpCode() {
        return httpCode;
    }
}
