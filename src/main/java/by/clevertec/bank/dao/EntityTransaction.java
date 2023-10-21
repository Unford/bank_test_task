package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The EntityTransaction class manages database transactions and provides methods for initializing, committing, and rolling
 * back transactions.
 */
@Getter
public class EntityTransaction implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger();
    private Connection connection;
    private final boolean isTransaction;



    public EntityTransaction(Connection connection, boolean isTransaction) {
        this.connection = connection;
        this.isTransaction = isTransaction;
    }

    public EntityTransaction(boolean isTransaction) {
        this(null, isTransaction);
    }


    @Override
    public void close() throws DaoException {
        try {
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            connection = null;
        }
    }


    /**
     * The function commits a transaction and logs an error message if the commit fails.
     */
    public void commit() {
        try {
            if (isTransaction) {
                connection.commit();
            }
        } catch (SQLException e) {
            logger.error("commit has been failed", e);
        }
    }


    /**
     * The function "rollback" attempts to rollback a transaction and logs an error message if it fails.
     */
    public void rollback() {
        try {
            if (isTransaction) {
                connection.rollback();
            }
        } catch (SQLException e) {
            logger.error("rollback has been failed", e);
        }
    }

    public boolean isTransaction() {
        return isTransaction;
    }

    public Connection getConnection() throws DaoException {
        try {
            if (connection == null) {
                connection = ConnectionPool.getInstance().getConnection();
            }
            connection.setAutoCommit(!isTransaction);
        } catch (SQLException e) {
            logger.error("error while getting connection from pool");
            throw new DaoException(e);
        }

        return connection;
    }
}
