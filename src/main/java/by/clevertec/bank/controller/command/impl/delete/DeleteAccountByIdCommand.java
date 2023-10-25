package by.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DeleteAccountByIdCommand class is a Java class that extends the Command class and is responsible for executing a
 * command to delete an account by its ID.
 */
public class DeleteAccountByIdCommand extends Command<Boolean, AccountService> {
    @Override
    public Boolean execute(HttpServletRequest request, AccountService service) throws CommandException {
        try {
            long id = extractIdParameter(request);
            return service.deleteById(id);
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
