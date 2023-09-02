package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
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
public class GetUserByIdCommand extends Command<UserDto> {
    @Override
    public UserDto execute(HttpServletRequest request) throws CommandException {
        UserServiceImpl service = UserServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            return service.findById(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
