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

@WebServlet(name = "transaction", value = "/transactions")
public class AccountTransactionServlet extends AbstractHttpServlet {
    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        String account = req.getParameter(RequestParameter.ACCOUNT);
        Command command;
        if (account != null) {
            command = CommandType.GET_ALL_TRANSACTIONS_BY_ACCOUNT.getCommand();
        } else {
            command = CommandType.GET_ALL_TRANSACTIONS.getCommand();
        }
        processCommand(command, req, resp);

    }

}
