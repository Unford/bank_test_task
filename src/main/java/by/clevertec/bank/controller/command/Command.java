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
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Command {
    protected static final Logger logger = LogManager.getLogger();

    protected <T> void validate(T value) throws CommandException {
        Validator validator = ValidatorHolder.getValidator();
        Set<ConstraintViolation<T>> validations = validator.validate(value);
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
    public abstract Object execute(HttpServletRequest request) throws CommandException;
}
