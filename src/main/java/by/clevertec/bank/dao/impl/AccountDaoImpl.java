
package by.clevertec.bank.dao.impl;

import by.clevertec.bank.dao.AbstractDao;
import by.clevertec.bank.dao.AccountDao;
import by.clevertec.bank.dao.ColumnName;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.domain.User;
import by.clevertec.bank.model.dto.MoneyStatsDto;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * The above code is implementing the AccountDao interface and providing the implementation for various methods such as
 * findAll, findById, create, update, deleteById, sumAllByAccountId, findAllAccrual, updateLastAccrualDate,
 * calculateMoneyDataAllByIdAndBetweenDates, and findByAccountOrBankAndUser. These methods perform database operations
 * related to managing bank accounts, such as retrieving all accounts, finding an account by its ID, creating a new
 * account, updating an existing account, deleting an account, calculating the sum of balances for a given account ID,
 * finding all accounts that need accrual,
 */
public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {


    private static final String SUM_BY_ID_QUERY = """
            SELECT SUM( CASE
                            WHEN owner_accounts_id = ? THEN sum
                            WHEN sender_account_id = ? THEN -sum
                            ELSE 0
                        END
                       ) AS total_balance
            FROM accounts_transactions
            WHERE owner_accounts_id = ? OR sender_account_id = ?;
            """;
    private static final String SUM_INCOME_EXPENDITURE_BY_ID_BETWEEN_DATES = """
            SELECT SUM( CASE
                            WHEN owner_accounts_id = ? THEN sum
                            WHEN sender_account_id = ? THEN -sum
                            ELSE 0
                        END
                       ) AS total_balance,
                   SUM( CASE
                            WHEN owner_accounts_id = ? AND sum > 0 THEN sum
                            ELSE 0
                       END
                       ) AS income,
                   SUM( CASE
                            WHEN sender_account_id = ? THEN -sum
                            WHEN sum < 0 THEN sum
                            ELSE 0
                       END
                       ) AS expenditure
            FROM accounts_transactions
            WHERE (owner_accounts_id = ? OR sender_account_id = ?)
              AND date BETWEEN  ? AND ?;
            """;

    private static final String FIND_ALL_ACCRUAL_QUERY = "SELECT * FROM bank_accounts WHERE (last_accrual_date < CURRENT_DATE)" +
            " AND EXTRACT(MONTH FROM last_accrual_date) < EXTRACT(MONTH FROM CURRENT_DATE)";
    private static final String UPDATE_ACCRUAL_DATE_QUERY = "UPDATE bank_accounts SET last_accrual_date = ?" +
            " WHERE bank_account_id = ?;";
    private static final String FIND_BY_ID_QUERY = """
            SELECT ba.*, b.name as name,
            u.full_name as full_name
            from bank_accounts ba
            join banks b on b.bank_id = ba.bank_id
            join users u on u.user_id = ba.user_id
            where bank_account_id = ?""";

    private static final String FIND_ALL_QUERY = "SELECT * FROM bank_accounts";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM bank_accounts WHERE bank_account_id = ?";
    private static final String CREATE_QUERY = "insert into bank_accounts (account, open_date, last_accrual_date," +
            " bank_id, user_id) values (?,?,?,?,?)";
    private static final String FIND_BY_ID_BANK_USER_QUERY = """
            SELECT ba.*, b.name as name,
            u.full_name as full_name
            from bank_accounts ba
            join banks b on b.bank_id = ba.bank_id
            join users u on u.user_id = ba.user_id
            where account = ? OR (ba.bank_id = ? AND ba.user_id = ?)""";
    private static final String UPDATE_QUERY = "UPDATE bank_accounts SET account = ? WHERE bank_account_id = ?";

    private static final String FIND_ALL_QUERY_BY_BANK = "SELECT * FROM bank_accounts WHERE bank_id = ?";

    private static final String FIND_ALL_QUERY_BY_USER = "SELECT * FROM bank_accounts WHERE user_id = ?";


    @Override
    public List<Account> findAll(Connection connection) throws DaoException {
        try {
            return performStatement(connection, FIND_ALL_QUERY, rs -> {
                List<Account> list = new ArrayList<>();
                while (rs.next()) {
                    Account v = mapEntity(rs);
                    list.add(v);
                }
                return list;
            });
        } catch (SQLException e) {
            logger.error("Find All accounts query error");
            throw new DaoException("Find All accounts query error", e);
        }

    }

    @Override
    public Optional<Account> findById(Connection connection, long id) throws DaoException {
        Optional<Account> account;
        try {
            account = Optional.ofNullable(performPreparedExecuteQuery(connection, FIND_BY_ID_QUERY,
                    s -> s.setLong(1, id),
                    rs -> rs.next() ? mapFullEntity(rs) : null));
        } catch (SQLException e) {
            logger.error("Find account by id query error");
            throw new DaoException("Find account by id query error", e);
        }
        return account;
    }

    @Override
    public Account create(Connection connection, Account entity) throws DaoException {
        LocalDate now = LocalDate.now();
        entity.setOpenDate(now);
        entity.setLastAccrualDate(now);
        try {
            long id = performPreparedUpdateReturnId(connection, CREATE_QUERY, s -> {
                s.setString(1, entity.getAccount());
                s.setDate(2, Date.valueOf(entity.getOpenDate()));
                s.setDate(3, Date.valueOf(entity.getLastAccrualDate()));
                s.setLong(4, entity.getBank().getId());
                s.setLong(5, entity.getUser().getId());
            });
            return findById(connection, id).orElse(entity);
        } catch (SQLException e) {
            logger.error("Create account query error", e);
            throw new DaoException("Create account query error", e);

        }
    }

    @Override
    public Account update(Connection connection, Account entity) throws DaoException {
        try {
            performPreparedUpdateReturnRows(connection, UPDATE_QUERY, s -> {
                s.setString(1, entity.getAccount());
                s.setLong(2, entity.getId());
            });
        } catch (SQLException e) {
            logger.error("Update account query error", e);
            throw new DaoException("Update account query error", e);

        }
        return findById(connection, entity.getId()).orElse(entity);
    }

    @Override
    public boolean deleteById(Connection connection, long id) throws DaoException {

        try {
            return performPreparedUpdateReturnRows(connection, DELETE_BY_ID_QUERY,
                    s -> s.setLong(1, id)) > 0;
        } catch (SQLException e) {
            logger.error("Delete account query error", e);
            throw new DaoException("Delete account query error", e);
        }


    }


    @Override
    protected Account mapEntity(ResultSet resultSet) throws SQLException {
        try {
            return Account.builder()
                    .id(resultSet.getLong(ColumnName.Account.ACCOUNT_ID))
                    .account(resultSet.getString(ColumnName.Account.ACCOUNT_NUM))
                    .lastAccrualDate(resultSet.getDate(ColumnName.Account.LAST_ACCRUAL_DATE).toLocalDate())
                    .openDate(resultSet.getDate(ColumnName.Account.OPEN_DATE).toLocalDate())
                    .bank(Bank.builder().id(resultSet.getLong(ColumnName.Account.BANK_ID)).build())
                    .user(User.builder().id(resultSet.getLong(ColumnName.Account.USER_ID)).build())
                    .build();
        } catch (SQLException e) {
            logger.error("mapping account error");
            throw e;
        }
    }

    @Override
    protected Account mapFullEntity(ResultSet resultSet) throws SQLException {
        try {
            return Account.builder()
                    .id(resultSet.getLong(ColumnName.Account.ACCOUNT_ID))
                    .account(resultSet.getString(ColumnName.Account.ACCOUNT_NUM))
                    .lastAccrualDate(resultSet.getDate(ColumnName.Account.LAST_ACCRUAL_DATE).toLocalDate())
                    .openDate(resultSet.getDate(ColumnName.Account.OPEN_DATE).toLocalDate())
                    .bank(Bank.builder()
                            .id(resultSet.getLong(ColumnName.Account.BANK_ID))
                            .name(resultSet.getString(ColumnName.Bank.BANK_NAME))
                            .build())
                    .user(User.builder()
                            .id(resultSet.getLong(ColumnName.Account.USER_ID))
                            .fullName(resultSet.getString(ColumnName.User.FULL_NAME)).build())
                    .build();
        } catch (SQLException e) {
            logger.error("mapping full account error");
            throw e;
        }
    }

    @Override
    public BigDecimal sumAllByAccountId(Connection connection, Long id) throws DaoException {
        BigDecimal sum;
        try {
            sum = performPreparedExecuteQuery(connection, SUM_BY_ID_QUERY,
                    s -> {
                        s.setLong(1, id);
                        s.setLong(2, id);
                        s.setLong(3, id);
                        s.setLong(4, id);
                    },
                    rs -> rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO);
        } catch (SQLException e) {
            logger.error("Sum all by id query error");
            throw new DaoException("Sum all by id query error", e);
        }
        return sum == null ? BigDecimal.ZERO : sum;
    }

    @Override
    public List<Account> findAllAccrual(Connection connection) throws DaoException {
        try {
            return performStatement(connection, FIND_ALL_ACCRUAL_QUERY, rs -> {
                List<Account> list = new ArrayList<>();
                while (rs.next()) {
                    Account v = mapEntity(rs);
                    list.add(v);
                }
                return list;
            });
        } catch (SQLException e) {
            logger.error("Find All accrual accounts query error");
            throw new DaoException("Find All accrual accounts query error", e);
        }
    }

    @Override
    public Account updateLastAccrualDate(Connection connection, Account account) throws DaoException {
        LocalDate now = LocalDate.now();
        try {
            logger.debug("Update account {} accrual date {}", account.getId(), now);
            performPreparedUpdateReturnRows(connection, UPDATE_ACCRUAL_DATE_QUERY, s -> {
                s.setDate(1, Date.valueOf(now));
                s.setLong(2, account.getId());
            });
            account.setLastAccrualDate(now);
        } catch (SQLException e) {
            logger.error("Update account accrual date error", e);
            throw new DaoException("Update account accrual date error", e);
        }
        return account;
    }

    @Override
    public MoneyStatsDto calculateMoneyDataAllByIdAndBetweenDates(Connection connection, Long id, LocalDate from, LocalDate to) throws DaoException {
        MoneyStatsDto stats;
        try {
            stats = performPreparedExecuteQuery(connection, SUM_INCOME_EXPENDITURE_BY_ID_BETWEEN_DATES, s -> {
                s.setLong(1, id);
                s.setLong(2, id);
                s.setLong(3, id);
                s.setLong(4, id);
                s.setLong(5, id);
                s.setLong(6, id);
                s.setDate(7, Date.valueOf(from));
                s.setDate(8, Date.valueOf(to.plusDays(1)));
            }, rs -> {
                MoneyStatsDto money = new MoneyStatsDto();
                if (rs.next()) {
                    money.setBalance(Optional.ofNullable(rs.getBigDecimal(1))
                            .orElse(BigDecimal.ZERO));
                    money.setIncome(Optional.ofNullable(rs.getBigDecimal(2))
                            .orElse(BigDecimal.ZERO));
                    money.setExpenditure(Optional.ofNullable(rs.getBigDecimal(3))
                            .orElse(BigDecimal.ZERO));
                }
                return money;
            });

        } catch (SQLException e) {
            logger.error("Sum All,income and expenditures by id between dates query error");
            throw new DaoException("Sum All,income and expenditures by id between dates query error", e);
        }
        return stats;
    }

    @Override
    public Optional<Account> findByAccountOrBankAndUser(Connection connection, String acc, long bankId, long userId) throws DaoException {
        Optional<Account> account;
        try {
            account = Optional.ofNullable(performPreparedExecuteQuery(connection, FIND_BY_ID_BANK_USER_QUERY, s -> {
                s.setString(1, acc);
                s.setLong(2, bankId);
                s.setLong(3, userId);
            }, rs -> rs.next() ? mapFullEntity(rs) : null));
        } catch (SQLException e) {
            logger.error("Find account by id bank and user query error");
            throw new DaoException("Find account by id bank and user query error", e);
        }
        return account;
    }

    @Override
    public List<Account> findAllByBankId(Connection connection, long id) throws DaoException {
        try {
            return performPreparedExecuteQuery(connection, FIND_ALL_QUERY_BY_BANK, s -> s.setLong(1, id), rs -> {
                List<Account> list = new ArrayList<>();
                while (rs.next()) {
                    Account v = mapEntity(rs);
                    list.add(v);
                }
                return list;
            });
        } catch (SQLException e) {
            logger.error("Find All accounts by bank query error");
            throw new DaoException("Find All accounts by bank query error", e);
        }
    }

    @Override
    public List<Account> findAllByUserId(Connection connection, long id) throws DaoException {
        try {
            return performPreparedExecuteQuery(connection, FIND_ALL_QUERY_BY_USER,
                    s -> s.setLong(1, id), rs -> {
                        List<Account> list = new ArrayList<>();
                        while (rs.next()) {
                            Account v = mapEntity(rs);
                            list.add(v);
                        }
                        return list;
                    });
        } catch (SQLException e) {
            logger.error("Find All accounts by user query error");
            throw new DaoException("Find All accounts by user query error", e);
        }
    }
}
