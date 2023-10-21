package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AccountTransactionService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

public class GetAllTransactionsByAccountIdCommand
        extends Command<Collection<TransactionDto>, AccountTransactionService> {
    @Override
    public Collection<TransactionDto> execute(HttpServletRequest request,
                                              AccountTransactionService service) throws CommandException {
        try {
            long id = extractIdParameter(request);
            return service.findAllByAccountId(id);
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
