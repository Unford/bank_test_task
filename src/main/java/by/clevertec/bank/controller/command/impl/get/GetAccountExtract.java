package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.AccountExtractDto;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The GetAccountExtract class is a Java command that retrieves an account extract based on the provided parameters.
 */
public class GetAccountExtract extends Command<AccountExtractDto, AccountService> {
    @Override
    public AccountExtractDto execute(HttpServletRequest request, AccountService service) throws CommandException {

        try {
            String fromStr = request.getParameter(RequestParameter.DATE_FROM);
            String toStr = request.getParameter(RequestParameter.DATE_TO);
            long id = extractIdParameter(request);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            AccountExtractDto statementDto = AccountExtractDto.builder().account(AccountDto.
                            builder()
                            .id(id).build())
                    .from(LocalDate.parse(fromStr, formatter))
                    .to(LocalDate.parse(toStr, formatter))
                    .build();
            return service.getAccountExtract(statementDto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        } catch (DateTimeParseException e) {
            throw new CommandException(CustomError.BAD_REQUEST, e);

        }
    }
}
