package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.BankDto;
import by.clevertec.bank.service.impl.BankServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

/**
 * The `GetAllBanksCommand` class is a Java command that retrieves all banks from a service and returns them as a
 * collection of `BankDto` objects.
 */
public class GetAllBanksCommand extends Command<Collection<BankDto>> {
    @Override
    public Collection<BankDto> execute(HttpServletRequest request) throws CommandException {
        BankServiceImpl service = BankServiceImpl.getInstance();
        try {
            return service.findAll();
        } catch (ServiceException e) {
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
