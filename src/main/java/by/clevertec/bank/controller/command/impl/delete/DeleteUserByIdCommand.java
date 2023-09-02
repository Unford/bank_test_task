package by.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DeleteUserByIdCommand class is a Java class that extends the Command class and is responsible for deleting a user by
 * their ID.
 */
public class DeleteUserByIdCommand extends Command<Boolean> {
    @Override
    public Boolean execute(HttpServletRequest request) throws CommandException {
        UserServiceImpl userService = UserServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            return userService.deleteById(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
