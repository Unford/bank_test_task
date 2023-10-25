package by.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.CrudService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The DefaultCommand class is a subclass of the Command class that throws a CommandException with a custom error when
 * executed.
 */
public class DefaultCommand extends Command {


    @Override
    public Object execute(HttpServletRequest request, CrudService service) throws CommandException {
        throw new CommandException("Unknown command", CustomError.NOT_FOUND);
    }
}
