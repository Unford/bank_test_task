package by.clevertec.bank.controller.command;

import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.util.DataMapper;
import by.clevertec.bank.util.ValidatorHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The above class is an abstract class in Java that provides common functionality for executing commands and handling
 * validation and reading of request bodies.
 */
public abstract class Command <S>{
    protected static final Logger logger = LogManager.getLogger();

    /**
     * The function "validate" is a protected method that takes a generic value as input and throws a CommandException if
     * the value is not valid according to the Default class.
     *
     * @param value The value that needs to be validated.
     */
    protected <T> void validate(T value) throws CommandException {
        this.validate(value, Default.class);

    }
    /**
     * The function validates a given value against a set of constraints and throws a CommandException if any violations
     * are found.
     *
     * @param value The `value` parameter is the object that needs to be validated. It can be of any type.
     */
    protected <T> void validate(T value, Class<?>... groups) throws CommandException {
        Validator validator = ValidatorHolder.getValidator();
        Set<ConstraintViolation<T>> validations = validator.validate(value, groups);
        if (!validations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            validations.forEach(v -> builder.append(v.getPropertyPath())
                    .append(" - ")
                    .append(v.getMessage())
                    .append(','));
            builder.deleteCharAt(builder.length() - 1);
            throw new CommandException(builder.toString(), CustomError.BAD_REQUEST);
        }

    }

    /**
     * The function reads the body of an HTTP request and converts it into an object of the specified class.
     *
     * @param request The `HttpServletRequest` object represents the HTTP request made by the client to the server. It
     * contains information such as the request method, headers, and body.
     * @param tClass The parameter `tClass` is a Class object that represents the type of the object that you want to
     * deserialize from the request body. It is used to specify the target type for deserialization using the `readValue`
     * method of the `ObjectMapper` class.
     * @return The method is returning an object of type T, which is determined by the generic type parameter T.
     */
    protected  <T> T readBody(HttpServletRequest request, Class<T> tClass) throws CommandException {
        String body = null;
        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            return DataMapper.getObjectMapper().readValue(body, tClass);
        } catch (IOException e) {
            logger.error("error while reading body", e);
            throw new CommandException("error while reading body", CustomError.BAD_REQUEST, e);
        }
    }
    /**
     * The function executes a command based on the given HttpServletRequest object and returns an Object.
     *
     * @param request The "request" parameter is an object of the HttpServletRequest class. It represents the client's
     * request to the server and contains information such as the HTTP method, request headers, request parameters, and
     * other data related to the request.
     * @return The method is returning an Object.
     */
    public abstract S execute(HttpServletRequest request) throws CommandException;
}
