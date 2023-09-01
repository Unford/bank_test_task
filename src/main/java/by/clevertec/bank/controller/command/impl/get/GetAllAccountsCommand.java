package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The `GetAllAccountsCommand` class is a Java class that extends the `Command` class and is responsible for executing a
 * command to retrieve all accounts from an `AccountService` and returning them.
 */
public class GetAllAccountsCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        AccountService accountService = AccountServiceImpl.getInstance();
        try {
            return accountService.findAll();
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
