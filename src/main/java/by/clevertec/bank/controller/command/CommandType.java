package by.clevertec.bank.controller.command;

import by.clevertec.bank.controller.ServletPath;
import by.clevertec.bank.controller.command.impl.DefaultCommand;
import by.clevertec.bank.controller.command.impl.DepositCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum CommandType {
    DEPOSIT(new DepositCommand(), ServletPath.TRANSACTION),
    DEFAULT_COMMAND(new DefaultCommand());


    private static final Logger logger = LogManager.getLogger();
    private final Command command;
    private final String path;

    CommandType(Command command, String path) {
        this.command = command;
        this.path = path;
    }

    CommandType(Command command) {
        this.command = command;
        this.path = "";
    }

    public static Command defineCommand(String parameter, String servletPath) {
        CommandType commandType = CommandType.DEFAULT_COMMAND;
        try {
            if (parameter != null) {
                commandType = CommandType.valueOf(parameter.toUpperCase());
                if (!commandType.getPath().equals(servletPath)) {
                    commandType = CommandType.DEFAULT_COMMAND;
                }
            }


        } catch (IllegalArgumentException e) {
            logger.error("Unknown command {}", parameter, e);
        }
        logger.debug(commandType);
        return commandType.getCommand();

    }

    public Command getCommand() {
        return command;
    }

    public String getPath() {
        return path;
    }

}
