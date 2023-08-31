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

@WebServlet(name = "account", value = "/accounts")
public class AccountServlet extends AbstractHttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter(RequestParameter.ID);
        Command command;
        if (id != null) {
            String comStr = req.getParameter(RequestParameter.COMMAND);
            if (CommandType.defineCommand(comStr, req.getServletPath())
                    .equals(CommandType.GET_ACCOUNT_BALANCE.getCommand())) {
                command = CommandType.GET_ACCOUNT_BALANCE.getCommand();
            }else {
                command = CommandType.GET_ACCOUNT_BY_ID.getCommand();

            }
        } else {
            command = CommandType.GET_ALL_ACCOUNTS.getCommand();
        }
        processCommand(command, req, resp);

    }
}
