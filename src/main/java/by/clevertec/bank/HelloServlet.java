package by.clevertec.bank;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World 11111111!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");

        Connection connection = connect();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM shop");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                BigDecimal cost = resultSet.getBigDecimal("cost");


                out.println("id: " + id);
                out.println("Name: " + cost.toString());
                out.println("\n===================\n");
            }
        } catch (SQLException e) {

        }

        // Hello

        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    public Connection connect() {//todo del

        final String url = "jdbc:postgresql://localhost:5432/shop";
        final String user = "root";
        final String password = "root";
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return conn;
    }

    public void destroy() {
    }
}