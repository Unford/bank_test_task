package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.UserDto;
import by.clevertec.bank.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The `GetUserByIdCommand` class is a Java class that extends the `Command` class and is responsible for executing a
 * command to retrieve a user by their ID.
 */
public class GetUserByIdCommand extends Command<UserDto, UserServiceImpl> {
    @Override
    public UserDto execute(HttpServletRequest request, UserServiceImpl service) throws CommandException {
        try {
            long id = extractIdParameter(request);
            return service.findById(id);
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
