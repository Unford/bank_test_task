package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The EntityTransaction class manages database transactions and provides methods for initializing, committing, and rolling
 * back transactions.
 */
public class EntityTransaction implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger();
    private Connection connection;


    /**
     * The function initializes a transaction by setting the auto commit to false.
     */
    public void initializeTransaction(AbstractDao... daos) throws DaoException {
        try {
            this.initialize(daos);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("Error while setting auto commit");
        }
    }


    /**
     * The function initializes a connection to a database and sets the connection for each provided DAO.
     */
    public void initialize(AbstractDao... daos) throws DaoException {
        if (connection == null) {
            try {
                connection = ConnectionPool.getInstance().getConnection();
                for (AbstractDao dao : daos) {
                    dao.setConnection(connection);
                }
            } catch (SQLException e) {
                logger.error("error while getting connection from pool");
                throw new DaoException(e);
            }
        }

    }

    @Override
    public void close() throws DaoException {
        try {
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
            connection.commit();
        } catch (SQLException e) {
            logger.error("commit has been failed", e);
        }
    }


    /**
     * The function "rollback" attempts to rollback a transaction and logs an error message if it fails.
     */
    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.error("rollback has been failed", e);
        }
    }
}
