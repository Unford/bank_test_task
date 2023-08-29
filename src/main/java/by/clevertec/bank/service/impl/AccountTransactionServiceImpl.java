package by.clevertec.bank.service.impl;

import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AccountTransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

public class AccountTransactionServiceImpl implements AccountTransactionService {
    private static final Logger logger = LogManager.getLogger();

    private AccountTransactionServiceImpl() {
    }

    private static final AccountTransactionServiceImpl instance = new AccountTransactionServiceImpl();

    public static AccountTransactionServiceImpl getInstance() {
        return instance;
    }

    @Override
    public AccountTransaction deposit(TransactionDto transactionDto) {
        logger.info(transactionDto);
        return null;
    }

    @Override
    public AccountTransaction withdrawal(BigDecimal sum, Account account) {
        return null;
    }

    @Override
    public AccountTransaction transferMoney(BigDecimal sum, Account from, Account to) {
        return null;
    }

    @Override
    public AccountTransaction accrueIncome(double percent) {
        return null;
    }
}
