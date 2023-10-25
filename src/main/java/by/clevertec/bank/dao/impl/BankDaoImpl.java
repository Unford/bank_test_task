package by.clevertec.bank.dao.impl;

import by.clevertec.bank.dao.AbstractDao;
import by.clevertec.bank.dao.BankDao;
import by.clevertec.bank.dao.ColumnName;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Bank;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * The BankDaoImpl class is an implementation of the BankDao interface that provides methods for performing CRUD operations
 * on a database table for banks.
 */
public class BankDaoImpl extends AbstractDao<Bank> implements BankDao {
    private static final String FIND_ALL_QUERY = "SELECT * FROM banks";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM banks WHERE bank_id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM banks WHERE name = ?";
    private static final String CREATE_QUERY = "insert into banks (name) values (?)";
    private static final String UPDATE_QUERY = "update banks SET name = ? WHERE bank_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM banks WHERE bank_id = ?";

    @Override
    public List<Bank> findAll(Connection connection) throws DaoException {
        try {
            return performStatement(connection, FIND_ALL_QUERY, rs -> {
                List<Bank> list = new ArrayList<>();
                while (rs.next()) {
                    Bank v = mapEntity(rs);
                    list.add(v);
                }
                return list;
            });
        } catch (SQLException e) {
            logger.error("Find All banks query error");
            throw new DaoException("Find All banks query error", e);
        }

    }

    @Override
    public Optional<Bank> findById(Connection connection, long id) throws DaoException {
        try {
            return Optional.ofNullable(performPreparedExecuteQuery(connection, FIND_BY_ID_QUERY,
                    s -> s.setLong(1, id),
                    rs -> rs.next() ? mapFullEntity(rs) : null));
        } catch (SQLException e) {
            logger.error("Find bank by id query error");
            throw new DaoException("Find bank by id query error", e);
        }

    }

    @Override
    public Bank create(Connection connection, Bank entity) throws DaoException {
        try {
            long id = performPreparedUpdateReturnId(connection, CREATE_QUERY, s -> s.setString(1, entity.getName()));
            entity.setId(id);
            return findById(connection, id).orElse(entity);
        } catch (SQLException e) {
            logger.error("Create bank query error", e);
            throw new DaoException("Create bank query error", e);
        }
    }

    @Override
    public Bank update(Connection connection, Bank entity) throws DaoException {
        try {
            performPreparedUpdateReturnRows(connection, UPDATE_QUERY, s -> {
                s.setString(1, entity.getName());
                s.setLong(2, entity.getId());
            });
        } catch (SQLException e) {
            logger.error("Update bank query error", e);
            throw new DaoException("Update bank query error", e);

        }
        return findById(connection, entity.getId()).orElse(entity);
    }

    @Override
    public boolean deleteById(Connection connection, long id) throws DaoException {
        try {
            return performPreparedUpdateReturnRows(connection, DELETE_BY_ID_QUERY,
                    s -> s.setLong(1, id)) > 0;
        } catch (SQLException e) {
            logger.error("Delete bank query error", e);
            throw new DaoException("Delete bank query error", e);
        }
    }

    @Override
    protected Bank mapEntity(ResultSet resultSet) throws SQLException {
        try {
            return Bank.builder()
                    .id(resultSet.getLong(ColumnName.Bank.BANK_ID))
                    .name(resultSet.getString(ColumnName.Bank.BANK_NAME))
                    .build();
        } catch (SQLException e) {
            logger.error("mapping bank error");
            throw e;
        }
    }


    @Override
    public Optional<Bank> findByName(Connection connection, String name) throws DaoException {
        Optional<Bank> bank;
        try {
            bank = Optional.ofNullable(performPreparedExecuteQuery(connection, FIND_BY_NAME_QUERY,
                    s -> s.setString(1, name),
                    rs -> rs.next() ? mapFullEntity(rs) : null));
        } catch (SQLException e) {
            logger.error("Find bank by name query error");
            throw new DaoException("Find bank by name query error", e);
        }
        return bank;
    }
}
