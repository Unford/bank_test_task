package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.UserDto;
import by.clevertec.bank.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

/**
 * The `GetAllUsersCommand` class is a Java class that extends the `Command` class and is responsible for executing a
 * command to retrieve all users from a service.
 */
public class GetAllUsersCommand extends Command<Collection<UserDto>, UserServiceImpl> {
    @Override
    public Collection<UserDto> execute(HttpServletRequest request, UserServiceImpl service) throws CommandException {
        try {
            return service.findAll();
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
