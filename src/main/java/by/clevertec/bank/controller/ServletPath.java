package by.clevertec.bank.controller;

import by.clevertec.bank.controller.impl.AccountServlet;
import by.clevertec.bank.controller.impl.AccountTransactionServlet;
import jakarta.servlet.annotation.WebServlet;

public final class ServletPath {
    private ServletPath(){}

    public static final String ACCOUNT = AccountServlet.class
            .getAnnotation(WebServlet.class).value()[0];

    public static final String TRANSACTION = AccountTransactionServlet.class
            .getAnnotation(WebServlet.class).value()[0];


}
