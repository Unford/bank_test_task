package by.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

public class GetAllAccountsCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        AccountService accountService = AccountServiceImpl.getInstance();
        try {
            return accountService.findAll();
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
