package by.clevertec.bank.controller;

import jakarta.servlet.annotation.WebServlet;

public final class ServletPath {
    private ServletPath(){}
    public static final String TRANSACTION = AccountTransactionServlet.class
            .getAnnotation(WebServlet.class).value()[0];


}
