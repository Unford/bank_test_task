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


    public abstract List<T> findAll() throws DaoException;


    public abstract Optional<T> findById(long id) throws DaoException;


    public abstract boolean create(T entity) throws DaoException;


    public abstract boolean update(T entity) throws DaoException;


    public abstract boolean deleteById(long id) throws DaoException;


    public boolean delete(T entity) throws DaoException {
        return deleteById(entity.getId());
    }

    protected abstract T mapEntity(ResultSet resultSet) throws SQLException;


    protected abstract void setPreparedStatement(PreparedStatement statement, T entity) throws DaoException;


    void setConnection(Connection connection) {
        this.connection = connection;
    }

}
