package by.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.impl.BankServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DeleteBankByIdCommand class is a Java class that extends the Command class and is responsible for deleting a bank by
 * its ID.
 */
public class DeleteBankByIdCommand extends Command<Boolean> {
    @Override
    public Boolean execute(HttpServletRequest request) throws CommandException {
        BankServiceImpl bankService = BankServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            return bankService.deleteById(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
