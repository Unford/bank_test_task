package by.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.impl.BankServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DeleteBankByIdCommand class is a Java class that extends the Command class and is responsible for deleting a bank by
 * its ID.
 */
public class DeleteBankByIdCommand extends Command<Boolean, BankServiceImpl> {
    @Override
    public Boolean execute(HttpServletRequest request, BankServiceImpl service) throws CommandException {
        try {
            long id = extractIdParameter(request);
            return service.deleteById(id);
        }catch (ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
