package by.clevertec.bank.controller.impl;


import by.clevertec.bank.controller.AbstractHttpServlet;
import by.clevertec.bank.controller.RequestParameter;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.controller.command.CommandType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The BankServlet class is a Java servlet that handles HTTP requests for banking operations such as updating, retrieving,
 * and creating banks.
 */
@WebServlet(name = "bank", value = "/banks")
public class BankServlet extends AbstractHttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.UPDATE_BANK_BY_ID.getCommand(), req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Command command = CommandType.defineCommand(req.getParameter(RequestParameter.COMMAND), req);
        if (command.equals(CommandType.DEFAULT_COMMAND.getCommand())) {
            if (req.getParameter(RequestParameter.ID) != null){
                command = CommandType.GET_BANK_BY_ID.getCommand();
            }else {
                command = CommandType.GET_ALL_BANKS.getCommand();
            }
        }
        processCommand(command, req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.CREATE_BANK.getCommand(), req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processCommand(CommandType.DELETE_BANK_BY_ID.getCommand(), req, resp);
    }
}
