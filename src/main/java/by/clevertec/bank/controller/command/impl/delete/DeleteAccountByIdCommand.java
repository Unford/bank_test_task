package by.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DeleteAccountByIdCommand class is a Java class that extends the Command class and is responsible for executing a
 * command to delete an account by its ID.
 */
public class DeleteAccountByIdCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        AccountService accountService = AccountServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            return accountService.deleteById(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
