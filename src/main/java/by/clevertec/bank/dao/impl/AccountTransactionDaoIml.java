package by.clevertec.bank.dao.impl;

import by.clevertec.bank.dao.AbstractDao;
import by.clevertec.bank.dao.AccountTransactionDao;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountTransactionDaoIml extends AbstractDao<AccountTransaction> implements AccountTransactionDao {
    private static final String ACCOUNT_TRANSACTION_ID = "id";
    private static final String ACCOUNT_TRANSACTION_SUM = "sum";
    private static final String ACCOUNT_TRANSACTION_DATE = "date";
    private static final String ACCOUNT_TRANSACTION_SENDER_ID = "sender_account_id";
    private static final String ACCOUNT_TRANSACTION_OWNER_ID = "owner_accounts_id";
    private static final String DEPOSIT_QUERY = "insert into accounts_transactions (sum, date, owner_accounts_id) values (?,?,?)";
    private static final String FIND_ALL_QUERY = "SELECT * from accounts_transactions";

    @Override
    public List<AccountTransaction> findAll() throws DaoException {
        List<AccountTransaction> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY)) {
                while (resultSet.next()){
                    AccountTransaction v = mapEntity(resultSet);
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.error("Find All ingredients query error");
            throw new DaoException("Find All ingredients query error", e);
        }
        return list;
    }

    @Override
    public Optional<AccountTransaction> findById(long id) throws DaoException {
        return Optional.empty();
    }

    @Override
    public boolean create(AccountTransaction entity) throws DaoException {
        return false;
    }

    @Override
    public boolean update(AccountTransaction entity) throws DaoException {
        return false;
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
        }catch (SQLException e){
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

        } catch (SQLException e) {
            logger.error("error while setting account transaction statement parameters", e);
            throw new DaoException("error while setting account transaction statement parameters");
        }
    }


    @Override
    public AccountTransaction deposit(AccountTransaction accountTransaction) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(DEPOSIT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            logger.debug(DEPOSIT_QUERY);
            accountTransaction.setDateTime(LocalDateTime.now());
            setPreparedStatement(statement, accountTransaction);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    accountTransaction.setId(resultSet.getLong(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Deposit query error", e);
            throw new DaoException("Deposit query error", e);

        }
        return accountTransaction;
    }
}
