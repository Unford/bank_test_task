package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.AccountExtractDto;
import by.clevertec.bank.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

/**
 * The GetAccountBalanceCommand class is a Java class that retrieves the balance of a specific account and returns it along
 * with the account ID in a DTO (Data Transfer Object).
 */
public class GetAccountBalanceCommand extends Command<AccountExtractDto, AccountService> {
    @Override
    public AccountExtractDto execute(HttpServletRequest request, AccountService service) throws CommandException {
        try {
            long id = extractIdParameter(request);
            BigDecimal balance = service.getAccountBalance(id);
            return AccountExtractDto.builder()
                    .balance(balance)
                    .account(AccountDto.builder()
                            .id(id)
                            .build())
                    .build();
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }

    }
}
