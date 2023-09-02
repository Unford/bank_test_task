package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.BankDto;
import by.clevertec.bank.service.impl.BankServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The GetBankByIdCommand class is a Java class that retrieves a bank by its ID from a service and returns it as a BankDto
 * object.
 */
public class GetBankByIdCommand extends Command<BankDto> {
    @Override
    public BankDto execute(HttpServletRequest request) throws CommandException {
        BankServiceImpl service = BankServiceImpl.getInstance();
        try {
            long id = Long.parseLong(request.getParameter(RequestParameter.ID));
            return service.findById(id);
        }catch (NumberFormatException | ServiceException e){
            logger.error(e);
            throw new CommandException(e);
        }
    }
}
