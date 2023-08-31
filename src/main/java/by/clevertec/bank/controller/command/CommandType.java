package by.clevertec.bank.controller.command;

import by.clevertec.bank.controller.ServletPath;
import by.clevertec.bank.controller.command.impl.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum CommandType {
    DEPOSIT(new DepositCommand(), ServletPath.TRANSACTION),
    WITHDRAWAL(new WithdrawalCommand(), ServletPath.TRANSACTION),
    TRANSFER(new TransferCommand(), ServletPath.TRANSACTION),


    GET_ALL_TRANSACTIONS(new GetAllTransactionsCommand()),
    DEFAULT_COMMAND(new DefaultCommand()),
    GET_ALL_TRANSACTIONS_BY_ACCOUNT(new GetAllTransactionsByAccountCommand()),
    GET_ACCOUNT_BALANCE(new GetAccountBalanceCommand(), ServletPath.ACCOUNT),
    GET_ACCOUNT_BY_ID(new GetAccountByIdCommand(), ServletPath.ACCOUNT),
    GET_ALL_ACCOUNTS(new GetAllAccountsCommand(),ServletPath.ACCOUNT );


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
        logger.debug("Command - {}, {}", commandType, servletPath);
        return commandType.getCommand();

    }

    public Command getCommand() {
        return command;
    }

    public String getPath() {
        return path;
    }

}
