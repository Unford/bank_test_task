package by.clevertec.bank.controller.command;

import by.clevertec.bank.exception.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Command {
    Logger logger = LogManager.getLogger();

    Object execute(HttpServletRequest request) throws CommandException;
}
