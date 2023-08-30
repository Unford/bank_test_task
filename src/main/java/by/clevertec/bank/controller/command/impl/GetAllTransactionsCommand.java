package by.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

public class GetAllTransactionsCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
        try {
            return service.findAll();
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
