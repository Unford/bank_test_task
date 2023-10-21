package test.clevertec.bank.service;

import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
class AbstractServiceTest {

    @InjectMocks
    AccountServiceImpl service;


    @Mock
    private AccountDaoImpl accountDao;


    @Mock
    private HikariDataSource dataSource;
    @Mock
    private Connection connection;

    @Test
    void shouldThrowServiceExceptionFromFunction() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findAll(Mockito.any())).thenThrow(new DaoException("test"));

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.findAll())
                    .isExactlyInstanceOf(ServiceException.class)
                    .withMessageContaining("test");


        }
    }
    @Test
    void shouldThrowServiceExceptionFromFunctionClose() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findAll(Mockito.any())).thenThrow(new DaoException("test"));
        Mockito.doThrow(new SQLException("test2")).when(connection).close();

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.findAll())
                    .isExactlyInstanceOf(ServiceException.class)
                    .withMessageContaining("test2");


        }

    }


    @Test
    void shouldThrowServiceExceptionFromConsumer() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findAllAccrual(Mockito.any())).thenThrow(new DaoException("test"));

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.accrueIncome(5))
                    .isExactlyInstanceOf(ServiceException.class)
                    .withMessageContaining("test");


        }

        Mockito.verify(accountDao, Mockito.times(1))
                .findAllAccrual(Mockito.any());

    }

    @Test
    void shouldThrowServiceExceptionFromConsumerClose() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.doThrow(new SQLException("test2")).when(connection).close();
        Mockito.when(accountDao.findAllAccrual(Mockito.any())).thenThrow(new DaoException("1"));

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.accrueIncome(5))
                    .isExactlyInstanceOf(ServiceException.class)
                    .withMessageContaining("test2");


        }

        Mockito.verify(accountDao, Mockito.times(1))
                .findAllAccrual(Mockito.any());

    }
}
