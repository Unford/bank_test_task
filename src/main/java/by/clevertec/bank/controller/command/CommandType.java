package by.clevertec.bank.controller.command;

import by.clevertec.bank.controller.command.impl.put.UpdateAccountCommand;
import by.clevertec.bank.controller.command.impl.post.CreateAccountCommand;
import by.clevertec.bank.controller.command.impl.DefaultCommand;
import by.clevertec.bank.controller.command.impl.delete.DeleteAccountByIdCommand;
import by.clevertec.bank.controller.command.impl.delete.DeleteTransactionByIdCommand;
import by.clevertec.bank.controller.command.impl.get.*;
import by.clevertec.bank.controller.command.impl.post.DepositCommand;
import by.clevertec.bank.controller.command.impl.post.TransferCommand;
import by.clevertec.bank.controller.command.impl.post.WithdrawalCommand;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static by.clevertec.bank.controller.HttpMethod.*;
import static by.clevertec.bank.controller.ServletPath.ACCOUNT;
import static by.clevertec.bank.controller.ServletPath.TRANSACTION;

/**
 * The `CommandType` enum is used to define and manage different types of commands in a banking application. Each command
 * is associated with a specific path and HTTP method.
 */
public enum CommandType {
    DEFAULT_COMMAND(new DefaultCommand()),

    DEPOSIT(new DepositCommand(), TRANSACTION, POST),
    WITHDRAWAL(new WithdrawalCommand(), TRANSACTION, POST),
    TRANSFER(new TransferCommand(), TRANSACTION, POST),
    GET_ALL_TRANSACTIONS(new GetAllTransactionsCommand(), TRANSACTION, GET),
    GET_ALL_TRANSACTIONS_BY_ACCOUNT(new GetAllTransactionsByAccountCommand(), TRANSACTION, GET),
    DELETE_TRANSACTION_BY_ID(new DeleteTransactionByIdCommand(), TRANSACTION, DELETE),


    GET_ACCOUNT_BALANCE(new GetAccountBalanceCommand(), ACCOUNT, GET),
    GET_ACCOUNT_BY_ID(new GetAccountByIdCommand(), ACCOUNT, GET),
    GET_ACCOUNT_EXTRACT(new GetAccountExtract(), ACCOUNT, GET),
    GET_ACCOUNT_STATEMENT(new GetAccountStatement(), ACCOUNT, GET),
    GET_ALL_ACCOUNTS(new GetAllAccountsCommand(), ACCOUNT, GET),
    DELETE_ACCOUNT_BY_ID(new DeleteAccountByIdCommand(), ACCOUNT, DELETE),
    CREATE_ACCOUNT(new CreateAccountCommand(), ACCOUNT, POST),
    UPDATE_ACCOUNT_BY_ID(new UpdateAccountCommand(),ACCOUNT , PUT);


    /**
     *  command type
     *
     */
    private static final Logger logger = LogManager.getLogger();
    private final Command command;
    private final String path;

    private final String method;

    CommandType(Command command, String path, String method) {
        this.command = command;
        this.path = path;
        this.method = method;
    }

    CommandType(Command command) {
        this(command, "", "");
    }

    public static Command defineCommand(String parameter, HttpServletRequest req) {
        CommandType commandType = CommandType.DEFAULT_COMMAND;
        try {
            if (parameter != null) {
                commandType = CommandType.valueOf(parameter.toUpperCase());
                if (!commandType.getPath().equals(req.getServletPath()) ||
                        !commandType.getMethod().equals(req.getMethod())) {
                    commandType = CommandType.DEFAULT_COMMAND;
                }
            }


        } catch (IllegalArgumentException e) {
            logger.error("Unknown command {}", parameter, e);
        }
        logger.debug("Command - {}, {}, {}", commandType, req.getServletPath(), req.getMethod());
        return commandType.getCommand();

    }

    public Command getCommand() {
        return command;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
