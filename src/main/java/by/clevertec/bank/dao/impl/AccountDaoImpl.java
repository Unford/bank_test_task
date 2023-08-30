package by.clevertec.bank.dao.impl;

import by.clevertec.bank.dao.AbstractDao;
import by.clevertec.bank.dao.AccountDao;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {
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
        return null;
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
        return sum;
    }
}
