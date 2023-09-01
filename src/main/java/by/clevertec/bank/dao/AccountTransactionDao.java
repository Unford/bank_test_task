package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.AccountTransaction;

import java.time.LocalDate;
import java.util.List;

public interface AccountTransactionDao {

    List<AccountTransaction> findAllByAccount(String account) throws DaoException;

    List<AccountTransaction> findAllByIdAndBetweenDates(long id, LocalDate from, LocalDate to) throws DaoException;
}
