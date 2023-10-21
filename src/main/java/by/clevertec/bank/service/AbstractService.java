package by.clevertec.bank.service;

import by.clevertec.bank.dao.EntityTransaction;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.BiThrowingConsumer;
import by.clevertec.bank.model.BiThrowingFunction;

import java.sql.Connection;

public abstract class AbstractService<E> implements CrudService<E> {
    protected <T> T executeInTransactionalContext(boolean isTransaction,
                                                  BiThrowingFunction<Connection, T, DaoException,
                                                          ServiceException> function)
            throws ServiceException {
        EntityTransaction transaction = new EntityTransaction(isTransaction);
        try {
            T res = function.apply(transaction.getConnection());
            transaction.commit();
            return res;
        } catch (DaoException e) {
            transaction.rollback();
            logger.error(e);
            throw new ServiceException(e);
        } finally {
            try {
                transaction.close();
            } catch (DaoException e) {
                throw new ServiceException(e);
            }
        }
    }

    protected <T> T executeInTransactionalContext(BiThrowingFunction<Connection, T, DaoException, ServiceException> function)
            throws ServiceException {
        return executeInTransactionalContext(false, function);
    }

    protected void executeInTransactionalContext(boolean isTransaction,
                                                 BiThrowingConsumer<Connection, DaoException, ServiceException> consumer)
            throws ServiceException {
        EntityTransaction transaction = new EntityTransaction(isTransaction);
        try {
            consumer.accept(transaction.getConnection());
            transaction.commit();
        } catch (DaoException e) {
            transaction.rollback();
            logger.error(e);
            throw new ServiceException(e);
        } finally {
            try {
                transaction.close();
            } catch (DaoException e) {
                throw new ServiceException(e);
            }
        }
    }


}
