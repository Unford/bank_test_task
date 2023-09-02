package by.clevertec.bank.controller;

import by.clevertec.bank.controller.impl.AccountServlet;
import by.clevertec.bank.controller.impl.AccountTransactionServlet;
import by.clevertec.bank.controller.impl.BankServlet;
import by.clevertec.bank.controller.impl.UserServlet;
import jakarta.servlet.annotation.WebServlet;

/**
 * The ServletPath class provides static final String variables representing the paths for different servlets.
 */
public final class ServletPath {
    private ServletPath(){}

    public static final String ACCOUNT = AccountServlet.class.getAnnotation(WebServlet.class).value()[0];
    public static final String TRANSACTION = AccountTransactionServlet.class.getAnnotation(WebServlet.class).value()[0];
    public static final String BANK = BankServlet.class.getAnnotation(WebServlet.class).value()[0];
    public static final String USER = UserServlet.class.getAnnotation(WebServlet.class).value()[0];


}
