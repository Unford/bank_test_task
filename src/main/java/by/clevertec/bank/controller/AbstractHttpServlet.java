package by.clevertec.bank.controller;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.util.DataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractHttpServlet extends HttpServlet {
    protected static final Logger logger = LogManager.getLogger();

    protected void processCommand(Command command, HttpServletRequest req,
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
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {
        String parameter = req.getParameter(RequestParameter.COMMAND);
        Command command = CommandType.defineCommand(parameter, req);
        processCommand(command, req, resp);
    }
}
