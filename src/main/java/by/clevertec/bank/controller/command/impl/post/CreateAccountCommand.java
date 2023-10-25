package by.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.validation.CreateAccountValidationGroup;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The CreateAccountCommand class is responsible for executing the create account command by reading the request body,
 * validating the account data, and calling the create method of the AccountServiceImpl class.
 */

public class CreateAccountCommand extends Command<AccountDto, AccountService> {
    @Override
    public AccountDto execute(HttpServletRequest request, AccountService service) throws CommandException {
        try {
            AccountDto dto = readBody(request, AccountDto.class);
            validate(dto, CreateAccountValidationGroup.class);
            return service.create(dto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
