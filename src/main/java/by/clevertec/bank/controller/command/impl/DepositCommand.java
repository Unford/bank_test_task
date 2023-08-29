package by.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import jakarta.servlet.http.HttpServletRequest;

public class DepositCommand implements Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {

        return null;
    }
}
