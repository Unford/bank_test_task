package by.clevertec.bank.service;

import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;

import java.math.BigDecimal;

public interface AccountTransactionService {
    AccountTransaction deposit(BigDecimal sum, Account account);

    AccountTransaction withdrawal(BigDecimal sum, Account account);

    AccountTransaction transferMoney(BigDecimal sum, Account from, Account to);
    AccountTransaction accrueIncome(double percent);


}
