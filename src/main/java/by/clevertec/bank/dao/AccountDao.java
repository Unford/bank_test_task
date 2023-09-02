package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.dto.MoneyStatsDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


// The `AccountDao` interface defines a set of methods that can be used to interact with the data access layer for managing
// bank accounts.
public interface AccountDao {
    /**
     * The function calculates the sum of all BigDecimal values associated with a given account ID.
     *
     * @param id The id parameter is a Long value representing the account id.
     * @return The method is returning a BigDecimal value.
     */
    BigDecimal sumAllByAccountId(Long id) throws DaoException;

    /**
     * The function returns a list of Account objects representing all accruals.
     *
     * @return The method is returning a list of Account objects.
     */
    List<Account> findAllAccrual() throws DaoException;

    /**
     * The function updates the last accrual date of an account and throws a DaoException if there is an error.
     *
     * @param account The account object represents a user's account information. It contains details such as account
     *                number, balance, and last accrual date.
     * @return The method is returning the last accrual date of the account.
     */
    Account updateLastAccrualDate(Account account) throws DaoException;

    /**
     * The function calculates money statistics for a specific ID within a given date range.
     *
     * @param id   The ID of the user or entity for which the money data is being calculated.
     * @param from The starting date for the calculation of money data.
     * @param to   The "to" parameter is a LocalDate object that represents the end date of the date range for which you want
     *             to calculate money data.
     * @return The method returns a MoneyStatsDto object.
     */
    MoneyStatsDto calculateMoneyDataAllByIdAndBetweenDates(Long id, LocalDate from, LocalDate to) throws DaoException;

    /**
     * The function findByAccountOrBankAndUser searches for an account based on the account number, bank ID, and user ID.
     *
     * @param acc    The account number or name to search for.
     * @param bankId The bankId parameter is a long value that represents the ID of the bank.
     * @param userId The ID of the user for which the account is being searched.
     * @return The method is returning an Optional object that contains an Account.
     */
    Optional<Account> findByAccountOrBankAndUser(String acc, long bankId, long userId) throws DaoException;


    /**
     * The function returns a list of objects that match a given bank ID.
     *
     * @param id The id parameter is a long value representing the bank id.
     * @return The method is returning a list of objects.
     */
    List<Account> findAllByBankId(long id) throws DaoException;

    /**
     * The function returns a list of accounts associated with a specific user ID.
     *
     * @param id The id parameter is a long value representing the user id.
     * @return The method is returning a list of Account objects.
     */
    List<Account> findAllByUserId(long id) throws DaoException;
}
