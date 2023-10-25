package test.clevertec.bank.dao;

import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.dao.EntityTransaction;
import by.clevertec.bank.exception.DaoException;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class EntityTransactionTest {
    @Mock
    private HikariDataSource dataSource;
    @Mock
    private Connection connection;


    @Test
    void shouldLogSqlExceptionWhileCommit() throws DaoException, SQLException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.doThrow(new SQLException()).when(connection).commit();
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);

            EntityTransaction entityTransaction = new EntityTransaction(true);
            entityTransaction.getConnection();
            Assertions.assertThatNoException().isThrownBy(entityTransaction::commit);


        }
    }

    @Test
    void shouldLogSqlExceptionWhileRollback() throws DaoException, SQLException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.doThrow(new SQLException()).when(connection).rollback();

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);

            EntityTransaction entityTransaction = new EntityTransaction(true);
            entityTransaction.getConnection();
            Assertions.assertThatNoException().isThrownBy(entityTransaction::rollback);

        }
    }

    @Test
    void shouldGetTransactionStatus() throws DaoException, SQLException {
        EntityTransaction falseEntity = new EntityTransaction(false);
        EntityTransaction trueEntity = new EntityTransaction(true);

        Assertions.assertThat(falseEntity.isTransaction()).isFalse();
        Assertions.assertThat(trueEntity.isTransaction()).isTrue();

    }

    @Test
    void shouldThrowDaoExceptionWhenGetConnection() throws DaoException, SQLException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.doThrow(new SQLException()).when(connection).setAutoCommit(Mockito.anyBoolean());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            EntityTransaction entityTransaction = new EntityTransaction(true);
            Assertions.assertThatException().isThrownBy(entityTransaction::getConnection)
                    .isExactlyInstanceOf(DaoException.class);


        }
    }

    @Test
    void shouldGetOnlyOneConnection() throws DaoException, SQLException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            EntityTransaction entityTransaction = new EntityTransaction(true);
            entityTransaction.getConnection();
            List<Connection> actual = Stream.generate(() -> {
                try {
                    return entityTransaction.getConnection();
                } catch (DaoException e) {
                    throw new RuntimeException(e);
                }
            }).limit(3).toList();

            Assertions.assertThat(actual).containsOnly(entityTransaction.getConnection());
        }
    }
}
