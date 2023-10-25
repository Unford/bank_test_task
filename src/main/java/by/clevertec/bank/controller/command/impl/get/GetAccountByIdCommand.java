package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The GetAccountByIdCommand class is a Java class that retrieves an account by its ID and returns it.
 */
public class GetAccountByIdCommand extends Command<AccountDto, AccountService> {
    @Override
    public AccountDto execute(HttpServletRequest request, AccountService service) throws CommandException {
        try {
            long id = extractIdParameter(request);
            return service.findById(id);
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }

    }
}
