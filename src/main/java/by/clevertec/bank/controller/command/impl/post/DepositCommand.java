package by.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AccountTransactionService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DepositCommand class is responsible for executing a deposit transaction on an account.
 */
public class DepositCommand extends Command<TransactionDto, AccountTransactionService> {

    @Override
    public TransactionDto execute(HttpServletRequest request, AccountTransactionService service)
            throws CommandException {
        try {
            TransactionDto transactionDto = readBody(request, TransactionDto.class);
            validate(transactionDto);
            return service.deposit(transactionDto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }


    }
}
