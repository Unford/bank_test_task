package by.clevertec.bank.controller.impl;

import by.clevertec.bank.controller.AbstractHttpServlet;
import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.ServiceName;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.service.CrudService;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The AccountTransactionServlet class is a Java servlet that handles HTTP requests for retrieving and deleting account
 * transactions.
 */
@WebServlet(name = "transaction", value = "/transactions")
public class AccountTransactionServlet extends AbstractHttpServlet {

    private transient AccountTransactionServiceImpl service;

    @Override
    public void init() throws ServletException {
        service = (AccountTransactionServiceImpl)
                getServletContext().getAttribute(ServiceName.TRANSACTION_SERVICE);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.DELETE_TRANSACTION_BY_ID.getCommand(), req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        Command command = CommandType.defineCommand(req.getParameter(RequestParameter.COMMAND), req);
        if (command.equals(CommandType.DEFAULT_COMMAND.getCommand())) {
            if (req.getParameter(RequestParameter.ID) != null) {
                command = CommandType.GET_TRANSACTION_BY_ID.getCommand();
            } else {
                command = CommandType.GET_ALL_TRANSACTIONS.getCommand();
            }
        }
        processCommand(command, req, resp);

    }

    @Override
    public CrudService<?> getBusinessService() {
        return service;
    }
}
