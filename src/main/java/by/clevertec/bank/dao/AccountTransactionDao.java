package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.AccountTransaction;

import java.util.List;

public interface AccountTransactionDao {
    AccountTransaction deposit(AccountTransaction accountTransaction) throws DaoException;

    List<AccountTransaction> findAllByAccount(String account) throws DaoException;
}
