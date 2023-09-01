package by.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.validation.CreateValidationGroup;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

public class CreateAccountCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        try {
            AccountServiceImpl service = AccountServiceImpl.getInstance();
            AccountDto dto = readBody(request, AccountDto.class);
            validate(dto, CreateValidationGroup.class);
            return service.create(dto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
