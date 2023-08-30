package by.clevertec.bank.dao.impl;

import by.clevertec.bank.dao.AbstractDao;
import by.clevertec.bank.dao.AccountTransactionDao;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountTransactionDaoIml extends AbstractDao<AccountTransaction> implements AccountTransactionDao {
    private static final String ACCOUNT_TRANSACTION_ID = "account_transaction_id";
    private static final String ACCOUNT_TRANSACTION_SUM = "sum";
    private static final String ACCOUNT_TRANSACTION_DATE = "date";
    private static final String ACCOUNT_TRANSACTION_SENDER_ID = "sender_account_id";
    private static final String ACCOUNT_TRANSACTION_OWNER_ID = "owner_accounts_id";
    private static final String CREATE_QUERY = "insert into accounts_transactions (sum, date, owner_accounts_id, sender_account_id) values (?,?,?,?)";
    private static final String FIND_ALL_BY_ACCOUNT_QUERY = "SELECT * from accounts_transactions INNER JOIN bank_accounts " +
            "ON accounts_transactions.account_transaction_id = bank_accounts.bank_account_id WHERE bank_accounts.account = ?";

    private static final String FIND_ALL_QUERY = "SELECT * from accounts_transactions";
    private static final String FIND_BY_ID_QUERY = "SELECT * from accounts_transactions where account_transaction_id = ?";




    @Override
    public List<AccountTransaction> findAll() throws DaoException {
        List<AccountTransaction> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY)) {
                while (resultSet.next()) {
                    AccountTransaction v = mapEntity(resultSet);
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.error("Find All transactions query error");
            throw new DaoException("Find All transactions query error", e);
        }
        return list;
    }

    @Override
    public Optional<AccountTransaction> findById(long id) throws DaoException {
        Optional<AccountTransaction> accountTransaction = Optional.empty();

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    accountTransaction = Optional.ofNullable(mapEntity(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Find transaction by id query error");
            throw new DaoException("Find transaction by id query error", e);
        }
        return accountTransaction;
    }

    @Override
    public AccountTransaction create(AccountTransaction accountTransaction) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            logger.debug(CREATE_QUERY);
            accountTransaction.setDateTime(LocalDateTime.now());
            setPreparedStatement(statement, accountTransaction);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    accountTransaction.setId(resultSet.getLong(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Create transaction query error", e);
            throw new DaoException("Create transaction query error", e);

        }
        return accountTransaction;
    }

    @Override
    public AccountTransaction update(AccountTransaction entity) throws DaoException {
        return null;
    }

    @Override
    public boolean deleteById(long id) throws DaoException {
        return false;
    }


    @Override
    protected AccountTransaction mapEntity(ResultSet resultSet) throws SQLException {
        try {
            return AccountTransaction.builder()
                    .id(resultSet.getLong(ACCOUNT_TRANSACTION_ID))
                    .dateTime(resultSet.getTimestamp(ACCOUNT_TRANSACTION_DATE).toLocalDateTime())
                    .sum(resultSet.getBigDecimal(ACCOUNT_TRANSACTION_SUM))
                    .to(Account.builder().id(resultSet.getLong(ACCOUNT_TRANSACTION_OWNER_ID)).build())
                    .from(Account.builder().id(resultSet.getLong(ACCOUNT_TRANSACTION_SENDER_ID)).build())
                    .build();
        } catch (SQLException e) {
            logger.error("mapping account transaction error");
            throw e;
        }
    }

    @Override
    protected void setPreparedStatement(PreparedStatement statement, AccountTransaction entity) throws DaoException {
        try {
            statement.setBigDecimal(1, entity.getSum());
            statement.setTimestamp(2, Timestamp.valueOf(entity.getDateTime()));
            statement.setLong(3, entity.getTo().getId());
            if (entity.getFrom() != null) {
                statement.setLong(4, entity.getFrom().getId());
            } else {
                statement.setNull(4, Types.BIGINT);
            }


        } catch (SQLException e) {
            logger.error("error while setting account transaction statement parameters", e);
            throw new DaoException("error while setting account transaction statement parameters");
        }
    }


    @Override
    public List<AccountTransaction> findAllByAccount(String account) throws DaoException {
        List<AccountTransaction> list = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_ACCOUNT_QUERY)) {
            statement.setString(1, account);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AccountTransaction v = mapEntity(resultSet);
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.error("Find All ingredients by account query error");
            throw new DaoException("Find All ingredients by account query error", e);
        }
        return list;
    }


}
