package by.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

public class DepositCommand extends Command {

    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        try {
            AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
            TransactionDto transactionDto = readBody(request, TransactionDto.class);
            validate(transactionDto);
            return service.deposit(transactionDto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }


    }
}
