package by.clevertec.bank.controller;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.controller.command.CommandType;
import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.util.JsonParser;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "transaction", value = "/transaction")
public class AccountTransactionServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM banks");
            while (resultSet.next()) {
                int id = resultSet.getInt("bank_id");
                String cost = resultSet.getString("name");


                out.println("id: " + id);
                out.println("Name: " + cost.toString());
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

        // Hello

        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {
        String parameter = req.getParameter(RequestParameter.COMMAND);
        Command command = CommandType.defineCommand(parameter, req.getServletPath());
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
        ObjectMapper mapper = JsonParser.getInstance();
        PrintWriter out = resp.getWriter();
        out.write(mapper.writeValueAsString(v));
        out.flush();
    }

}
