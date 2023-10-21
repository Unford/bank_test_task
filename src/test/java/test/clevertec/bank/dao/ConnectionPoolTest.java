package test.clevertec.bank.dao;

import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.exception.ServiceException;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.Driver;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ConnectionPoolTest {


    @Test
    void shouldCreateOnlyOneInstance() throws ServiceException, ExecutionException, InterruptedException, SQLException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<HikariDataSource>> actual = executorService.invokeAll(Stream.generate(() ->
                (Callable<HikariDataSource>) () -> {
                    try (MockedConstruction<HikariDataSource> mocked =
                                 Mockito.mockConstruction(HikariDataSource.class, (mock, context) -> {
                                 })) {
                        return ConnectionPool.getInstance();

                    }

                }).limit(4).toList());

        Assertions.assertThat(actual)
                .extracting(Future::get)
                .allSatisfy((e) -> Assertions.assertThat(e).isEqualTo(ConnectionPool.getInstance()));


        try (MockedStatic<DriverManager> managerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            managerMockedStatic.when(DriverManager::getDrivers).thenReturn(Collections.emptyEnumeration());
            ConnectionPool.close();
        }
    }

    @Test
    void shouldCreateOnlyOnePoolInstanceAndClose() throws ServiceException, ExecutionException, InterruptedException, SQLException {


        try (MockedConstruction<HikariDataSource> mocked =
                     Mockito.mockConstruction(HikariDataSource.class, (mock, context) -> {
                     })) {

            HikariDataSource instance = ConnectionPool.getInstance();
            HikariDataSource instance2 = ConnectionPool.getInstance();
            HikariDataSource instance3 = ConnectionPool.getInstance();
            try (MockedStatic<DriverManager> managerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
                managerMockedStatic.when(DriverManager::getDrivers).thenReturn(Collections.emptyEnumeration());
                ConnectionPool.close();
            }

            Assertions.assertThat(mocked.constructed().size()).isOne();

        }


    }

    @Test
    void shouldNotCloseEmptyDataSource() {
        Assertions.assertThatNoException().isThrownBy(ConnectionPool::close);
    }


    @Test
    void shouldCloseAndDeregisterDriver() throws SQLException {
        try (MockedConstruction<HikariDataSource> mocked =
                     Mockito.mockConstruction(HikariDataSource.class, (mock, context) -> {
                     })) {

            HikariDataSource instance = ConnectionPool.getInstance();
            instance.getConnection();

            try (MockedStatic<DriverManager> managerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
                managerMockedStatic.when(DriverManager::getDrivers)
                        .thenReturn(Collections.enumeration(List.of(new Driver())));


                ConnectionPool.close();
            }

            Assertions.assertThat(mocked.constructed().size()).isOne();

        }
    }

    @Test
    void shouldCloseDeregisterDriverThrowSQLException() throws SQLException {
        try (MockedConstruction<HikariDataSource> mocked =
                     Mockito.mockConstruction(HikariDataSource.class, (mock, context) -> {
                     })) {

            HikariDataSource instance = ConnectionPool.getInstance();
            instance.getConnection();

            try (MockedStatic<DriverManager> managerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
                managerMockedStatic.when(DriverManager::getDrivers)
                        .thenReturn(Collections.enumeration(List.of(new Driver())));
                managerMockedStatic.when(() -> DriverManager.deregisterDriver(Mockito.any()))
                        .thenThrow(SQLException.class);

                ConnectionPool.close();
            }

            Assertions.assertThat(mocked.constructed().size()).isOne();

        }
    }


}
