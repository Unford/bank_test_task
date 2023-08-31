package by.clevertec.bank.dao.impl;

import by.clevertec.bank.dao.AbstractDao;
import by.clevertec.bank.dao.AccountDao;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.domain.User;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {
    private static final String ACCOUNT_ID = "bank_account_id";
    private static final String ACCOUNT_NUM = "account";
    private static final String OPEN_DATE = "open_date";
    private static final String LAST_ACCRUAL_DATE = "last_accrual_date";
    private static final String BANK_ID = "bank_id";
    private static final String USER_ID = "user_id";

    private static final String SUM_BY_ID = """
            SELECT SUM( CASE
                            WHEN owner_accounts_id = ? THEN sum
                            WHEN sender_account_id = ? THEN -sum
                            ELSE 0
                        END
                       ) AS total_balance
            FROM accounts_transactions
            WHERE owner_accounts_id = ? OR sender_account_id = ?;
            """;
    private static final String FIND_ALL_ACCRUAL_QUERY = "SELECT * FROM bank_accounts WHERE (last_accrual_date < CURRENT_DATE)" +
            " AND EXTRACT(MONTH FROM last_accrual_date) < EXTRACT(MONTH FROM CURRENT_DATE)";
    private static final String UPDATE_ACCRUAL_DATE_QUERY = "UPDATE bank_accounts SET last_accrual_date = ?" +
            " WHERE bank_account_id = ?;";

    @Override
    public List<Account> findAll() throws DaoException {
        return null;
    }

    @Override
    public Optional<Account> findById(long id) throws DaoException {
        return Optional.empty();
    }

    @Override
    public Account create(Account entity) throws DaoException {
        return null;
    }

    @Override
    public Account update(Account entity) throws DaoException {
        return null;
    }

    @Override
    public boolean deleteById(long id) throws DaoException {
        return false;
    }

    @Override
    protected Account mapEntity(ResultSet resultSet) throws SQLException {
        try {
            return Account.builder()
                    .id(resultSet.getLong(ACCOUNT_ID))
                    .account(resultSet.getString(ACCOUNT_NUM))
                    .lastAccrualDate(resultSet.getDate(LAST_ACCRUAL_DATE).toLocalDate())
                    .openDate(resultSet.getDate(OPEN_DATE).toLocalDate())
                    .bank(Bank.builder().id(resultSet.getLong(BANK_ID)).build())
                    .owner(User.builder().id(resultSet.getLong(USER_ID)).build())
                    .build();
        } catch (SQLException e) {
            logger.error("mapping account error");
            throw e;
        }
    }

    @Override
    protected void setPreparedStatement(PreparedStatement statement, Account entity) throws DaoException {

    }

    @Override
    public BigDecimal sumAllByAccountId(Long id) throws DaoException {
        BigDecimal sum = BigDecimal.ZERO;
        try (PreparedStatement statement = connection.prepareStatement(SUM_BY_ID)) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setLong(3, id);
            statement.setLong(4, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    sum = resultSet.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Sum All by id query error");
            throw new DaoException("Sum All by id query error", e);
        }
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public List<Account> findAllAccrual() throws DaoException {
        List<Account> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(FIND_ALL_ACCRUAL_QUERY)) {
                while (resultSet.next()) {
                    Account v = mapEntity(resultSet);
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.error("Find All accrual accounts query error");
            throw new DaoException("Find All accrual accounts query error", e);
        }
        return list;
    }

    @Override
    public Account updateLastAccrualDate(Account account) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_ACCRUAL_DATE_QUERY)) {
            LocalDate now = LocalDate.now();
            logger.debug("Update account {} accrual date {}", account.getId(),now);
            statement.setDate(1, Date.valueOf(now));
            statement.setLong(2, account.getId());
            statement.executeUpdate();
            account.setLastAccrualDate(now);
        } catch (SQLException e) {
            logger.error("Update account accrual date error", e);
            throw new DaoException("Update account accrual date error", e);
        }
        return account;
    }
}
