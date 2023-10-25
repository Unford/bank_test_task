package by.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AccountTransactionService;
import by.clevertec.bank.model.validation.TransferValidationGroup;
import jakarta.servlet.http.HttpServletRequest;

import javax.validation.groups.Default;

/**
 * The TransferCommand class is a Java class that extends the Command class and is responsible for executing a transfer of
 * money between two accounts.
 */
public class TransferCommand extends Command<TransactionDto, AccountTransactionService> {
    @Override
    public TransactionDto execute(HttpServletRequest request, AccountTransactionService service) throws CommandException {
        try {
            TransactionDto transactionDto = readBody(request, TransactionDto.class);
            if (transactionDto.getTo().getId().equals(transactionDto.getFrom().getId())){
                throw new CommandException("to.id can not be equals from.id", CustomError.BAD_REQUEST);
            }
            validate(transactionDto, TransferValidationGroup.class, Default.class);
            return service.transferMoney(transactionDto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
