package by.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

public class DepositCommand extends Command {

    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
        TransactionDto transactionDto = readBody(request, TransactionDto.class);
        validate(transactionDto);
        return service.deposit(transactionDto);


    }
}
