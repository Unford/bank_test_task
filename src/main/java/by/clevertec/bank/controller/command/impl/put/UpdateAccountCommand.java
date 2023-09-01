package by.clevertec.bank.controller.command.impl.put;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.validation.UpdateValidationGroup;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import javax.validation.groups.Default;

/**
 * The UpdateAccountCommand class is a Java class that extends the Command class and is responsible for executing the
 * update operation on an account.
 */
public class UpdateAccountCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        try {
            AccountServiceImpl service = AccountServiceImpl.getInstance();
            AccountDto dto = readBody(request, AccountDto.class);
            validate(dto, UpdateValidationGroup.class, Default.class);
            return service.update(dto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
