package by.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.AccountTransactionService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DeleteTransactionByIdCommand class is a Java class that extends the Command class and is responsible for executing a
 * command to delete an account transaction by its ID.
 */
public class DeleteTransactionByIdCommand extends Command<Boolean, AccountTransactionService> {
    @Override
    public Boolean execute(HttpServletRequest request, AccountTransactionService service) throws CommandException {
        try {
            long id = extractIdParameter(request);
            return service.deleteById(id);
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
