package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.AccountExtractDto;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public class GetAccountBalanceCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        AccountService accountService = AccountServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            BigDecimal balance = accountService.getAccountBalance(id);
            return AccountExtractDto.builder().balance(balance)
                    .account(AccountDto.builder().id(id).build()).build();
        } catch (NumberFormatException | ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }

    }
}
