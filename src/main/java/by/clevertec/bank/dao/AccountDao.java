package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;

import java.math.BigDecimal;

public interface AccountDao {
    BigDecimal sumAllByAccountId(Long id) throws DaoException;

}
