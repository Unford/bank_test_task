package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

/**
 * The `GetAllAccountsCommand` class is a Java class that extends the `Command` class and is responsible for executing a
 * command to retrieve all accounts from an `AccountService` and returning them.
 */
public class GetAllAccountsCommand extends Command<Collection<AccountDto>, AccountService> {
    @Override
    public Collection<AccountDto> execute(HttpServletRequest request, AccountService service) throws CommandException {
        try {
            return service.findAll();
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
