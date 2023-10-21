package by.clevertec.bank.controller.command.impl.put;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.UserDto;
import by.clevertec.bank.model.validation.UpdateValidationGroup;
import by.clevertec.bank.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import javax.validation.groups.Default;

/**
 * The UpdateUserByIdCommand class is a Java class that updates a user by their ID using data from an HTTP request.
 */
public class UpdateUserByIdCommand extends Command<UserDto, UserServiceImpl> {
    @Override
    public UserDto execute(HttpServletRequest request, UserServiceImpl service) throws CommandException {
        try {
            UserDto dto = readBody(request, UserDto.class);
            validate(dto, UpdateValidationGroup.class, Default.class);
            return service.update(dto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
