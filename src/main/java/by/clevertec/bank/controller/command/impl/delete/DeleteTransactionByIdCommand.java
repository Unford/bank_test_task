package by.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DeleteTransactionByIdCommand class is a Java class that extends the Command class and is responsible for executing a
 * command to delete an account transaction by its ID.
 */
public class DeleteTransactionByIdCommand extends Command<Boolean> {
    @Override
    public Boolean execute(HttpServletRequest request) throws CommandException {
        AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            return service.deleteById(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
