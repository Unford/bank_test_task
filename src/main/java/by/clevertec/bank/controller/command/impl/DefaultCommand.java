package by.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.model.dto.CustomError;
import jakarta.servlet.http.HttpServletRequest;

public class DefaultCommand implements Command {
    @Override
    public Object execute(HttpServletRequest request) throws CommandException {
        throw new CommandException("Unknown command", CustomError.NOT_FOUND);
    }
}
