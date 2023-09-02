package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.AccountStatementDto;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The GetAccountStatement class is a Java command that retrieves an account statement based on the provided parameters.
 */
public class GetAccountStatement extends Command<AccountStatementDto> {
    @Override
    public AccountStatementDto execute(HttpServletRequest request) throws CommandException {
        AccountService service = AccountServiceImpl.getInstance();
        try {
            String idStr = request.getParameter(RequestParameter.ID);
            String fromStr = request.getParameter(RequestParameter.DATE_FROM);
            String toStr = request.getParameter(RequestParameter.DATE_TO);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            AccountStatementDto statementDto = AccountStatementDto.builder()
                    .account(AccountDto.builder().id(Long.parseLong(idStr)).build())
                    .from(LocalDate.parse(fromStr, formatter))
                    .to(LocalDate.parse(toStr, formatter))
                    .build();
            return service.getAccountStatement(statementDto);
        } catch (ServiceException | DateTimeParseException | NumberFormatException e) {
            throw new CommandException(e);
        }
    }
}
