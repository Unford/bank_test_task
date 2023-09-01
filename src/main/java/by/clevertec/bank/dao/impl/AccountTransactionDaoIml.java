package by.clevertec.bank.dao.impl;

import by.clevertec.bank.dao.AbstractDao;
import by.clevertec.bank.dao.AccountTransactionDao;
import by.clevertec.bank.dao.ColumnName;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.domain.User;
import by.clevertec.bank.model.domain.Bank;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.clevertec.bank.dao.ColumnName.*;

public class AccountTransactionDaoIml extends AbstractDao<AccountTransaction> implements AccountTransactionDao {

    private static final String CREATE_QUERY = "insert into accounts_transactions (sum, date, owner_accounts_id, sender_account_id) values (?,?,?,?)";
    private static final String FIND_ALL_BY_ACCOUNT_QUERY = "SELECT * from accounts_transactions INNER JOIN bank_accounts ba " +
            "ON accounts_transactions.sender_account_id = ba.bank_account_id OR " +
            "accounts_transactions.owner_accounts_id = ba.bank_account_id join banks b on b.bank_id = ba.bank_id join users u on u.user_id = ba.user_id WHERE ba.account = ?";

    private static final String FIND_ALL_QUERY = "SELECT * from accounts_transactions";
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                at.*,
                ba_sender.account AS sender_account,
                b_sender.name AS sender_bank,
                u_sender.full_name AS sender_full_name,
                ba_owner.account AS owner_account,
                b_owner.name AS owner_bank,
                u_owner.full_name AS owner_full_name
            FROM accounts_transactions at
                     LEFT JOIN bank_accounts ba_sender ON ba_sender.bank_account_id = at.sender_account_id
                     LEFT JOIN bank_accounts ba_owner ON ba_owner.bank_account_id = at.owner_accounts_id
                     LEFT JOIN banks b_sender ON b_sender.bank_id = ba_sender.bank_id
                     LEFT JOIN banks b_owner ON b_owner.bank_id = ba_owner.bank_id
                     LEFT JOIN users u_sender ON u_sender.user_id = ba_sender.user_id
                     LEFT JOIN users u_owner ON u_owner.user_id = ba_owner.user_id
            WHERE account_transaction_id = ?;""";
    private static final String FIND_ALL_BY_ACCOUNT_ID_AND_DATES_QUERY = FIND_ALL_QUERY +
            " where (owner_accounts_id = ? OR sender_account_id = ?) AND date BETWEEN ? AND ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM accounts_transactions " +
            "WHERE account_transaction_id = ?";


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
                    accountTransaction = Optional.ofNullable(mapFullEntity(resultSet));
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
        return findById(accountTransaction.getId()).orElse(accountTransaction);
    }

    @Override
    public AccountTransaction update(AccountTransaction entity) throws DaoException {
        throw new UnsupportedOperationException("Update query is forbidden for account transaction dao");
    }

    @Override
    public boolean deleteById(long id) throws DaoException {
        int res;
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {
            statement.setLong(1, id);
            res = statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Delete transaction query error", e);
            throw new DaoException("Delete transaction query error", e);
        }
        return res > 0;
    }


    @Override
    protected AccountTransaction mapEntity(ResultSet resultSet) throws SQLException {
        try {
            return AccountTransaction.builder()
                    .id(resultSet.getLong(Transaction.ACCOUNT_TRANSACTION_ID))
                    .dateTime(resultSet.getTimestamp(Transaction.ACCOUNT_TRANSACTION_DATE).toLocalDateTime())
                    .sum(resultSet.getBigDecimal(Transaction.ACCOUNT_TRANSACTION_SUM))
                    .to(Account.builder().id(resultSet.getLong(Transaction.ACCOUNT_TRANSACTION_OWNER_ID)).build())
                    .from(Account.builder().id(resultSet.getLong(Transaction.ACCOUNT_TRANSACTION_SENDER_ID)).build())
                    .build();
        } catch (SQLException e) {
            logger.error("mapping account transaction error");
            throw e;
        }
    }

    private AccountTransaction mapFullEntity(ResultSet rs) throws SQLException {
        try {
            return AccountTransaction.builder()
                    .id(rs.getLong(Transaction.ACCOUNT_TRANSACTION_ID))
                    .dateTime(rs.getTimestamp(Transaction.ACCOUNT_TRANSACTION_DATE).toLocalDateTime())
                    .sum(rs.getBigDecimal(Transaction.ACCOUNT_TRANSACTION_SUM))
                    .from(Account.builder()
                            .id(rs.getLong(Transaction.ACCOUNT_TRANSACTION_SENDER_ID))
                            .user(User.builder().fullName(rs.getString(ColumnName.User.SENDER_FULL_NAME)).build())
                            .bank(Bank.builder().name(rs.getString(ColumnName.Bank.SENDER_BANK_NAME)).build())
                            .account(rs.getString(ColumnName.Account.SENDER_ACCOUNT))
                            .build())
                    .to(Account.builder()
                            .id(rs.getLong(Transaction.ACCOUNT_TRANSACTION_OWNER_ID))
                            .user(User.builder().fullName(rs.getString(ColumnName.User.OWNER_FULL_NAME)).build())
                            .bank(Bank.builder().name(rs.getString(ColumnName.Bank.OWNER_BANK_NAME)).build())
                            .account(rs.getString(ColumnName.Account.OWNER_ACCOUNT))
                            .build())
                    .build();
        } catch (SQLException e) {
            logger.error("mapping full account transaction error");
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

    @Override
    public List<AccountTransaction> findAllByIdAndBetweenDates(long id, LocalDate from, LocalDate to) throws DaoException{
        List<AccountTransaction> list = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_ACCOUNT_ID_AND_DATES_QUERY)) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setDate(3, Date.valueOf(from));
            statement.setDate(4, Date.valueOf(to.plusDays(1)));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AccountTransaction v = mapEntity(resultSet);
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.error("Find All transactions by account id and between dates query error");
            throw new DaoException("Find All transactions by account id and between dates query error", e);
        }
        return list;
    }


}
