package by.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.BankDto;
import by.clevertec.bank.model.validation.CreateValidationGroup;
import by.clevertec.bank.service.impl.BankServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The CreateBankCommand class is a Java class that extends the Command class and is responsible for executing the creation
 * of a bank using the BankServiceImpl class.
 */
public class CreateBankCommand extends Command<BankDto> {
    @Override
    public BankDto execute(HttpServletRequest request) throws CommandException {
        try {
            BankServiceImpl service = BankServiceImpl.getInstance();
            BankDto dto = readBody(request, BankDto.class);
            validate(dto, CreateValidationGroup.class);
            return service.create(dto);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
