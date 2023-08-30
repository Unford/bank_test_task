package by.clevertec.bank.controller;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.util.DataMapper;
import by.clevertec.bank.util.ValidatorHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "transaction", value = "/transactions")
public class AccountTransactionServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();

    private void processCommand(Command command, HttpServletRequest req,
                                HttpServletResponse resp) throws IOException {
        Object v;
        try {
            v = command.execute(req);
        } catch (CommandException e) {
            logger.error(e);
            v = CustomError.builder()
                    .code(e.getHttpCode())
                    .message(e.getMessage())
                    .build();
            resp.setStatus(e.getHttpCode());
        }
        ObjectMapper mapper = DataMapper.getObjectMapper();
        PrintWriter out = resp.getWriter();
        out.write(mapper.writeValueAsString(v));
        out.flush();
    }

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

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {
        String parameter = req.getParameter(RequestParameter.COMMAND);
        Command command = CommandType.defineCommand(parameter, req.getServletPath());
        processCommand(command, req, resp);
    }

}
