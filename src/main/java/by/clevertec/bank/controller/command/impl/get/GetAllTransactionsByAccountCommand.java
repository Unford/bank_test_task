package by.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

public class GetAllTransactionsByAccountCommand extends Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        AccountTransactionServiceImpl service = AccountTransactionServiceImpl.getInstance();
        try {
            return service.findAllByAccount(request.getParameter(RequestParameter.ACCOUNT));
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}
