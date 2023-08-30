package by.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

public class WithdrawalCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        try {
            AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
            TransactionDto transactionDto = readBody(request, TransactionDto.class);
            validate(transactionDto);
            return service.withdrawal(transactionDto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
