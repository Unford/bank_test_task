package by.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.UserDto;
import by.clevertec.bank.model.validation.CreateValidationGroup;
import by.clevertec.bank.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The `CreateUserCommand` class is a Java class that extends the `Command` class and is responsible for creating a new
 * user by executing the necessary operations.
 */
public class CreateUserCommand extends Command<UserDto> {
    @Override
    public UserDto execute(HttpServletRequest request) throws CommandException {
        try {
            UserServiceImpl service = UserServiceImpl.getInstance();
            UserDto dto = readBody(request, UserDto.class);
            validate(dto, CreateValidationGroup.class);
            return service.create(dto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
