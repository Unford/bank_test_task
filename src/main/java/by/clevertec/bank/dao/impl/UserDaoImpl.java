package by.clevertec.bank.dao.impl;

import by.clevertec.bank.dao.AbstractDao;
import by.clevertec.bank.dao.ColumnName;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The `UserDaoImpl` class is an implementation of a data access object (DAO) for managing user data in a database.
 */
public class UserDaoImpl extends AbstractDao<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String CREATE_QUERY = "insert into users (full_name) values (?)";
    private static final String UPDATE_QUERY = "update users SET full_name = ? WHERE user_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE user_id = ?";

    @Override
    public List<User> findAll() throws DaoException {
        try {
            return performStatement(FIND_ALL_QUERY, rs -> {
                List<User> list = new ArrayList<>();
                while (rs.next()) {
                    User v = mapEntity(rs);
                    list.add(v);
                }
                return list;
            });
        } catch (SQLException e) {
            logger.error("Find All user query error");
            throw new DaoException("Find All user query error", e);
        }
    }

    @Override
    public Optional<User> findById(long id) throws DaoException {
        try {
            return Optional.ofNullable(performPreparedExecuteQuery(FIND_BY_ID_QUERY,
                    s -> s.setLong(1, id),
                    rs -> rs.next() ? mapFullEntity(rs) : null));
        } catch (SQLException e) {
            logger.error("Find user by id query error");
            throw new DaoException("Find user by id query error", e);
        }
    }

    @Override
    public User create(User entity) throws DaoException {
        try {
            long id = performPreparedUpdateReturnId(CREATE_QUERY,
                    s -> s.setString(1, entity.getFullName()));
            entity.setId(id);
            return findById(id).orElse(entity);
        } catch (SQLException e) {
            logger.error("Create user query error", e);
            throw new DaoException("Create user query error", e);
        }
    }

    @Override
    public User update(User entity) throws DaoException {
        try {
            performPreparedUpdateReturnRows(UPDATE_QUERY, s -> {
                s.setString(1, entity.getFullName());
                s.setLong(2, entity.getId());
            });
        } catch (SQLException e) {
            logger.error("Update user query error", e);
            throw new DaoException("Update user query error", e);

        }
        return findById(entity.getId()).orElse(entity);
    }

    @Override
    public boolean deleteById(long id) throws DaoException {
        try {
            return performPreparedUpdateReturnRows(DELETE_BY_ID_QUERY,
                    s -> s.setLong(1, id)) > 0;
        } catch (SQLException e) {
            logger.error("Delete user query error", e);
            throw new DaoException("Delete user query error", e);
        }
    }

    @Override
    protected User mapEntity(ResultSet resultSet) throws SQLException {
        try {
            return User.builder()
                    .id(resultSet.getLong(ColumnName.User.USER_ID))
                    .fullName(resultSet.getString(ColumnName.User.FULL_NAME))
                    .build();
        } catch (SQLException e) {
            logger.error("mapping user error");
            throw e;
        }
    }

}
