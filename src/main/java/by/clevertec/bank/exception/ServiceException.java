package by.clevertec.bank.exception;

import by.clevertec.bank.model.dto.CustomError;

/**
 * The ServiceException class is a custom exception that can be used to handle service-related errors, with an optional
 * HTTP code.
 */
public class ServiceException extends Exception {
    private int httpCode = CustomError.INTERNAL;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }


    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, int code) {
        super(message);
        this.httpCode = code;
    }


    public int getHttpCode() {
        return httpCode;
    }
}
