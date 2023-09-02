package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The GetTransactionById class is a Java command that retrieves a transaction by its ID from an account transaction
 * service.
 */
public class GetTransactionById extends Command<TransactionDto> {
    @Override
    public TransactionDto execute(HttpServletRequest request) throws CommandException {
        AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            return service.findById(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
