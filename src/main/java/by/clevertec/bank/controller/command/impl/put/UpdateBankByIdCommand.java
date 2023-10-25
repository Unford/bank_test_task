package by.clevertec.bank.controller.command.impl.put;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.BankDto;
import by.clevertec.bank.model.validation.UpdateValidationGroup;
import by.clevertec.bank.service.impl.BankServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import javax.validation.groups.Default;

/**
 * The UpdateBankByIdCommand class is a Java class that extends the Command class and is responsible for executing the
 * update operation on a bank entity based on its ID.
 */
public class UpdateBankByIdCommand extends Command<BankDto, BankServiceImpl> {
    @Override
    public BankDto execute(HttpServletRequest request, BankServiceImpl service) throws CommandException {
        try {
            BankDto dto = readBody(request, BankDto.class);
            validate(dto, UpdateValidationGroup.class, Default.class);
            return service.update(dto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
