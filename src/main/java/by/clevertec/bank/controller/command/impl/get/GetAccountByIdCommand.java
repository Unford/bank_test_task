package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The GetAccountByIdCommand class is a Java class that retrieves an account by its ID and returns it.
 */
public class GetAccountByIdCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        AccountService accountService = AccountServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            return accountService.findById(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }

    }
}
