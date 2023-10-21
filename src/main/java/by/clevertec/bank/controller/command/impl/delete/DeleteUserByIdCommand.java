package by.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DeleteUserByIdCommand class is a Java class that extends the Command class and is responsible for deleting a user by
 * their ID.
 */
public class DeleteUserByIdCommand extends Command<Boolean, UserServiceImpl> {
    @Override
    public Boolean execute(HttpServletRequest request, UserServiceImpl service) throws CommandException {
        try {
            long id = extractIdParameter(request);
            return service.deleteById(id);
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
