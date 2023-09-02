package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.AccountTransaction;

import java.time.LocalDate;
import java.util.List;


public interface AccountTransactionDao {

    /**
     * The function returns a list of AccountTransaction objects that are associated with a specific account.
     *
     * @param account The account parameter is a String that represents the account for which we want to find all the
     * account transactions.
     * @return The method is returning a list of AccountTransaction objects.
     */
    List<AccountTransaction> findAllByAccount(String account) throws DaoException;

    /**
     * The function finds all account transactions by a given ID and within a specified date range.
     *
     * @param id The ID of the account for which transactions need to be found.
     * @param from The starting date for the search.
     * @param to The "to" parameter is the end date of the date range for which you want to find account transactions.
     * @return The method is returning a list of AccountTransaction objects.
     */
    List<AccountTransaction> findAllByIdAndBetweenDates(long id, LocalDate from, LocalDate to) throws DaoException;

    /**
     * The function returns a list of AccountTransaction objects that belong to a specific account ID.
     *
     * @param id The id parameter is a long value representing the account id.
     * @return The method `findAllByAccountId` returns a list of `AccountTransaction` objects.
     */
    List<AccountTransaction> findAllByAccountId(long id) throws DaoException;
}
