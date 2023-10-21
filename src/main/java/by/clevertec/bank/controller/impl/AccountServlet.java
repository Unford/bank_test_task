
package by.clevertec.bank.controller.impl;


import by.clevertec.bank.controller.AbstractHttpServlet;
import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.ServiceName;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.service.CrudService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The AccountServlet class is a Java servlet that handles HTTP requests for account-related operations such as creating,
 * updating, deleting, and retrieving accounts.
 */
@WebServlet(name = "account", value = "/accounts")
public class AccountServlet extends AbstractHttpServlet {
    private transient AccountService service;

    @Override
    public void init() throws ServletException {
        service = (AccountService) getServletContext().getAttribute(ServiceName.ACCOUNT_SERVICE);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.DELETE_ACCOUNT_BY_ID.getCommand(), req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.UPDATE_ACCOUNT_BY_ID.getCommand(), req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Command command = CommandType.defineCommand(req.getParameter(RequestParameter.COMMAND), req);
        if (command.equals(CommandType.DEFAULT_COMMAND.getCommand())) {
            if (req.getParameter(RequestParameter.ID) != null) {
                command = CommandType.GET_ACCOUNT_BY_ID.getCommand();
            } else {
                command = CommandType.GET_ALL_ACCOUNTS.getCommand();
            }

        }
        processCommand(command, req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.CREATE_ACCOUNT.getCommand(), req, resp);
    }

    @Override
    public CrudService<?> getBusinessService() {
        return service;
    }
}
