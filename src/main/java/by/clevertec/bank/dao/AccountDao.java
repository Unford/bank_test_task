package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    BigDecimal sumAllByAccountId(Long id) throws DaoException;

    List<Account> findAllAccrual() throws DaoException;

    Account updateLastAccrualDate(Account account) throws DaoException;
}
