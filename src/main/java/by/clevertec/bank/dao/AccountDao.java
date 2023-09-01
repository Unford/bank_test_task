package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.dto.MoneyStatsDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface AccountDao {
    BigDecimal sumAllByAccountId(Long id) throws DaoException;

    List<Account> findAllAccrual() throws DaoException;

    Account updateLastAccrualDate(Account account) throws DaoException;

    MoneyStatsDto calculateMoneyDataAllByIdAndBetweenDates(Long id, LocalDate from, LocalDate to) throws DaoException;

    Optional<Account> findByAccountOrBankAndUser(String acc, long bankId, long userId) throws DaoException;
}
