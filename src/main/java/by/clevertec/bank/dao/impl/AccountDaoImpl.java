
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



// The above code is implementing the AccountDao interface and providing the implementation for various methods such as
// findAll, findById, create, update, deleteById, sumAllByAccountId, findAllAccrual, updateLastAccrualDate,
// calculateMoneyDataAllByIdAndBetweenDates, and findByAccountOrBankAndUser. These methods perform database operations
// related to managing bank accounts, such as retrieving all accounts, finding an account by its ID, creating a new
// account, updating an existing account, deleting an account, calculating the sum of balances for a given account ID,
// finding all accounts that need accrual,
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
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM bank_accounts " +
            "WHERE bank_account_id = ?";
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


    @Override
    public List<Account> findAll() throws DaoException {
        List<Account> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY)) {
                while (resultSet.next()) {
                    Account v = mapEntity(resultSet);
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.error("Find All accounts query error");
            throw new DaoException("Find All accounts query error", e);
        }
        return list;
    }

    @Override
    public Optional<Account> findById(long id) throws DaoException {
        Optional<Account> account = Optional.empty();

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    account = Optional.ofNullable(mapFullEntity(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Find account by id query error");
            throw new DaoException("Find account by id query error", e);
        }
        return account;
    }

    @Override
    public Account create(Account entity) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            LocalDate now = LocalDate.now();
            entity.setOpenDate(now);
            entity.setLastAccrualDate(now);
            setPreparedStatement(statement, entity);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getLong(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Create account query error", e);
            throw new DaoException("Create account query error", e);

        }
        return findById(entity.getId()).orElse(entity);
    }

    @Override
    public Account update(Account entity) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setString(1, entity.getAccount());
            statement.setLong(2, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Update account query error", e);
            throw new DaoException("Update account query error", e);

        }
        return findById(entity.getId()).orElse(entity);
    }

    @Override
    public boolean deleteById(long id) throws DaoException {
        int res;
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {
            statement.setLong(1, id);
            res = statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Delete account query error", e);
            throw new DaoException("Delete account query error", e);
        }
        return res > 0;
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


    private Account mapFullEntity(ResultSet resultSet) throws SQLException {
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
            logger.error("mapping account error");
            throw e;
        }
    }

    @Override
    protected void setPreparedStatement(PreparedStatement statement, Account entity) throws DaoException {
        try {
            statement.setString(1, entity.getAccount());
            statement.setDate(2, Date.valueOf(entity.getOpenDate()));
            statement.setDate(3, Date.valueOf(entity.getLastAccrualDate()));
            statement.setLong(4, entity.getBank().getId());
            statement.setLong(5, entity.getUser().getId());
        } catch (SQLException e) {
            logger.error("error while setting account statement parameters", e);
            throw new DaoException("error while setting account statement parameters");
        }
    }

    @Override
    public BigDecimal sumAllByAccountId(Long id) throws DaoException {
        BigDecimal sum = BigDecimal.ZERO;
        try (PreparedStatement statement = connection.prepareStatement(SUM_BY_ID_QUERY)) {
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
            logger.debug("Update account {} accrual date {}", account.getId(), now);
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

    @Override
    public MoneyStatsDto calculateMoneyDataAllByIdAndBetweenDates(Long id, LocalDate from, LocalDate to) throws DaoException {
        MoneyStatsDto stats = new MoneyStatsDto();
        try (PreparedStatement statement = connection.prepareStatement(SUM_INCOME_EXPENDITURE_BY_ID_BETWEEN_DATES)) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setLong(3, id);
            statement.setLong(4, id);
            statement.setLong(5, id);
            statement.setLong(6, id);
            statement.setDate(7, Date.valueOf(from));
            statement.setDate(8, Date.valueOf(to.plusDays(1)));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    stats.setBalance(Optional.ofNullable(resultSet.getBigDecimal(1))
                            .orElse(BigDecimal.ZERO));
                    stats.setIncome(Optional.ofNullable(resultSet.getBigDecimal(2))
                            .orElse(BigDecimal.ZERO));
                    stats.setExpenditure(Optional.ofNullable(resultSet.getBigDecimal(3))
                            .orElse(BigDecimal.ZERO));
                }
            }
        } catch (SQLException e) {
            logger.error("Sum All,income and expenditures by id between dates query error");
            throw new DaoException("Sum All,income and expenditures by id between dates query error", e);
        }
        return stats;
    }

    @Override
    public Optional<Account> findByAccountOrBankAndUser(String acc, long bankId, long userId) throws DaoException {
        Optional<Account> account = Optional.empty();

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_BANK_USER_QUERY)) {
            statement.setString(1, acc);
            statement.setLong(2, bankId);
            statement.setLong(3, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    account = Optional.ofNullable(mapFullEntity(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Find account by id bank and user query error");
            throw new DaoException("Find account by id bank and user query error", e);
        }
        return account;
    }
}
