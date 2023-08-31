package by.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import by.clevertec.bank.util.MoneyTransferGroup;
import jakarta.servlet.http.HttpServletRequest;

import javax.validation.groups.Default;

public class TransferCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        try {
            AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
            TransactionDto transactionDto = readBody(request, TransactionDto.class);
            if (transactionDto.getTo().getId().equals(transactionDto.getFrom().getId())){
                throw new CommandException("to.id can not be equals from.id", CustomError.BAD_REQUEST);
            }
            validate(transactionDto, MoneyTransferGroup.class, Default.class);
            return service.transferMoney(transactionDto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
