package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

/**
 * The `GetAllTransactionsByAccountCommand` class is a Java class that extends the `Command` class and is responsible for
 * executing a command to retrieve all transactions for a specific account.
 */
public class GetAllTransactionsByAccountCommand extends Command<Collection<TransactionDto>> {
    @Override
    public Collection<TransactionDto> execute(HttpServletRequest request) throws CommandException {
        AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
        try {
            return service.findAllByAccount(request.getParameter(RequestParameter.ACCOUNT));
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
