package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.AbstractDaoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
     * found.
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
     * the database.
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
     * the database.
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
     * query. It contains the data retrieved from the database and allows you to access and manipulate that data. In this
     * case, it is used as input to the mapEntity method, which is responsible for mapping the data from the
     * @return The method is returning an object of type T.
     */
    protected abstract T mapEntity(ResultSet resultSet) throws SQLException;


    /**
     * The function sets the values of a PreparedStatement object using the properties of an entity object.
     *
     * @param statement The PreparedStatement object that will be used to execute the SQL statement.
     * @param entity The entity parameter represents the object that contains the data to be set in the PreparedStatement.
     * It is of type T, which is a generic type that can be replaced with any specific class or interface.
     */
    protected abstract void setPreparedStatement(PreparedStatement statement, T entity) throws DaoException;


    /**
     * The function sets the connection for the current object.
     *
     * @param connection The "connection" parameter is of type "Connection". It is used to set the value of the
     * "connection" instance variable in the current object.
     */
    void setConnection(Connection connection) {
        this.connection = connection;
    }

}
