package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AccountTransactionService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The GetTransactionById class is a Java command that retrieves a transaction by its ID from an account transaction
 * service.
 */
public class GetTransactionById extends Command<TransactionDto, AccountTransactionService> {
    @Override
    public TransactionDto execute(HttpServletRequest request, AccountTransactionService service)
            throws CommandException {
        try {
            long id = extractIdParameter(request);
            return service.findById(id);
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
