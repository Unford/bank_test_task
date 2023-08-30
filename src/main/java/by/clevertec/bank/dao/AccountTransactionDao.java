package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.AccountTransaction;

public interface AccountTransactionDao {
    AccountTransaction deposit(AccountTransaction accountTransaction) throws DaoException;
}
