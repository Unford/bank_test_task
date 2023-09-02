package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

public class GetAllTransactionsByAccountIdCommand extends Command<Collection<TransactionDto>> {
    @Override
    public Collection<TransactionDto> execute(HttpServletRequest request) throws CommandException {
        AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ACCOUNT_ID));
            return service.findAllByAccountId(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
