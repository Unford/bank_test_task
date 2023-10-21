package by.clevertec.bank.controller.impl;

import by.clevertec.bank.controller.AbstractHttpServlet;
import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.ServiceName;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.service.CrudService;
import by.clevertec.bank.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The UserServlet class is a Java servlet that handles HTTP requests for user-related operations such as creating,
 * updating, and retrieving users.
 */
@WebServlet(name = "user", value = "/users")
public class UserServlet extends AbstractHttpServlet {
    private transient UserServiceImpl service;

    @Override
    public void init() {
        service = (UserServiceImpl) getServletContext().getAttribute(ServiceName.USER_SERVICE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.UPDATE_USER_BY_ID.getCommand(), req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Command command = CommandType.defineCommand(req.getParameter(RequestParameter.COMMAND), req);
        if (command.equals(CommandType.DEFAULT_COMMAND.getCommand())) {
            if (req.getParameter(RequestParameter.ID) != null) {
                command = CommandType.GET_USER_BY_ID.getCommand();
            } else {
                command = CommandType.GET_ALL_USERS.getCommand();
            }
        }
        processCommand(command, req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.CREATE_USER.getCommand(), req, resp);
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.DELETE_USER_BY_ID.getCommand(), req, resp);
    }

    @Override
    public CrudService<?> getBusinessService() {
        return service;
    }
}
