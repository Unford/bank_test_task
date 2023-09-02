package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.AbstractDaoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T extends AbstractDaoEntity> {
    protected static final Logger logger = LogManager.getLogger();


    protected Connection connection;


    /**
     * The function `findAll()` returns a list of objects of type T and throws a DaoException if an error occurs.
     *
     * @return A List of objects of type T is being returned.
     */
    public abstract List<T> findAll() throws DaoException;


    /**
     * The function findById returns an Optional object containing an entity of type T with the specified id, or an empty
     * Optional if no entity is found.
     *
     * @param id The parameter "id" is of type long and represents the unique identifier of the entity that needs to be
     *           found.
     * @return The method is returning an Optional object of type T.
     */
    public abstract Optional<T> findById(long id) throws DaoException;


    /**
     * The function creates a new entity and returns it, while also throwing a DaoException if an error occurs.
     *
     * @param entity The parameter "entity" represents an object of type T that is being created.
     * @return The method is returning an object of type T.
     */
    public abstract T create(T entity) throws DaoException;


    /**
     * The function updates an entity of type T and throws a DaoException if an error occurs.
     *
     * @param entity The "entity" parameter is an object of type T, which represents the entity that needs to be updated in
     *               the database.
     * @return The method is returning an object of type T.
     */
    public abstract T update(T entity) throws DaoException;


    /**
     * The function deletes a record from a database table based on the provided ID.
     *
     * @param id The id parameter is a long value that represents the unique identifier of the object to be deleted.
     * @return A boolean value is being returned.
     */
    public abstract boolean deleteById(long id) throws DaoException;


    /**
     * The function deletes an entity by its ID and returns a boolean indicating whether the deletion was successful.
     *
     * @param entity The parameter "entity" is an object of type T, which represents an entity that you want to delete from
     *               the database.
     * @return The method is returning a boolean value.
     */
    public boolean delete(T entity) throws DaoException {
        return deleteById(entity.getId());
    }

    /**
     * The function "mapEntity" is an abstract method that takes a ResultSet as input and returns an object of type T,
     * while also potentially throwing a SQLException.
     *
     * @param resultSet The resultSet parameter is a ResultSet object that represents a set of results from a database
     *                  query. It contains the data retrieved from the database and allows you to access and manipulate that data. In this
     *                  case, it is used as input to the mapEntity method, which is responsible for mapping the data from the
     * @return The method is returning an object of type T.
     */
    protected abstract T mapEntity(ResultSet resultSet) throws SQLException;

    /**
     * The function "mapFullEntity" maps a ResultSet to an entity object of type T.
     *
     * @param resultSet The `resultSet` parameter is a `ResultSet` object, which represents a set of results from a
     *                  database query. It contains the data retrieved from the database and provides methods to access and manipulate that
     *                  data.
     * @return The method is returning an object of type T.
     */
    protected T mapFullEntity(ResultSet resultSet) throws SQLException {
        return mapEntity(resultSet);
    }

    /**
     * The function sets the connection for the current object.
     *
     * @param connection The "connection" parameter is of type "Connection". It is used to set the value of the
     *                   "connection" instance variable in the current object.
     */
    void setConnection(Connection connection) {
        this.connection = connection;
    }

    protected long performPreparedUpdateReturn(String query,
                                               ThrowingBiConsumer<PreparedStatement, T, SQLException> setter,
                                               T entity, int autoGeneratedKeys) throws SQLException {
        long res;
        try (PreparedStatement statement = connection.prepareStatement(query, autoGeneratedKeys)) {
            setter.accept(statement, entity);
            res = statement.executeUpdate();
            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        return resultSet.getLong(1);
                    }
                }
            }

        }
        return res;

    }

    protected long performPreparedUpdateReturnId(String query,
                                                 ThrowingConsumer<PreparedStatement, SQLException> setter)
            throws SQLException {
        return performPreparedUpdateReturn(query, (f, s) -> setter.accept(f), null, Statement.RETURN_GENERATED_KEYS);

    }

    protected long performPreparedUpdateReturnRows(String query,
                                                   ThrowingConsumer<PreparedStatement, SQLException> setter)
            throws SQLException {
        return performPreparedUpdateReturn(query, (f, s) -> setter.accept(f), null, Statement.NO_GENERATED_KEYS);
    }

    protected <V> V performPreparedExecuteQuery(String query,
                                                ThrowingBiConsumer<PreparedStatement, T, SQLException> setter,
                                                T entity, ThrowingFunction<ResultSet, V, SQLException> mapper)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            setter.accept(statement, entity);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapper.apply(resultSet);
            }
        }
    }

    protected <V> V performPreparedExecuteQuery(String query,
                                                ThrowingConsumer<PreparedStatement, SQLException> setter,
                                                ThrowingFunction<ResultSet, V, SQLException> mapper)
            throws SQLException {
        return performPreparedExecuteQuery(query, (s, v) -> setter.accept(s), null, mapper);
    }

    protected <V> V performStatement(String query,
                                     ThrowingFunction<ResultSet, V, SQLException> mapper)
            throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                return mapper.apply(resultSet);
            }
        }
    }
}
