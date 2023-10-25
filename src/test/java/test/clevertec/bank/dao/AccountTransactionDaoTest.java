package test.clevertec.bank.dao;

import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;
import test.clevertec.bank.common.DataGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class AccountTransactionDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.9")
            .withInitScript("init_script.sql");
    Connection connection;
    AccountTransactionDaoIml dao = new AccountTransactionDaoIml();

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(postgres.getJdbcUrl(),
                postgres.getUsername(), postgres.getPassword());

    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void shouldFindTransaction() throws DaoException {
        Account from = new Account();
        from.setBank(new Bank());
        from.setUser(new User());
        from.setId(0L);
        AccountTransaction expected = new AccountTransaction(1L, BigDecimal.valueOf(100.00).setScale(2),
                LocalDateTime.of(2023, 8, 15, 10, 30, 0),
                new Account(1L, "1111111111", null, null,
                        new Bank(null, "Bank A"),
                        new User(null, "John Doe")),
                from
        );
        Optional<AccountTransaction> actual = dao.findById(connection, expected.getId());
        Assertions.assertThat(actual)
                .isPresent().get().isEqualTo(expected);
    }

    @Test
    void shouldNotFindTransaction() throws DaoException {
        Optional<AccountTransaction> actual = dao.findById(connection, 1000L);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void shouldNotFindTransactionThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findById(connection, 1))
                .isInstanceOf(DaoException.class);
    }


    @Test
    void shouldDeleteTransactionById() throws DaoException {
        AccountTransaction generatedTransaction = DataGenerator.generateTransaction();
        generatedTransaction.setTo(Account.builder()
                .id(1L)
                .build());
        generatedTransaction.setFrom(null);
        AccountTransaction transaction = dao.create(connection, generatedTransaction);
        int expected = dao.findAll(connection).size() - 1;

        boolean result = dao.deleteById(connection, transaction.getId());
        Assertions.assertThat(result).isTrue();
        int actual = dao.findAll(connection).size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldDeleteTransaction() throws DaoException {
        AccountTransaction generatedTransaction = DataGenerator.generateTransaction();
        generatedTransaction.setTo(Account.builder()
                .id(1L)
                .build());
        generatedTransaction.setFrom(null);
        AccountTransaction transaction = dao.create(connection, generatedTransaction);
        int expected = dao.findAll(connection).size() - 1;

        boolean result = dao.delete(connection, transaction);
        Assertions.assertThat(result).isTrue();
        int actual = dao.findAll(connection).size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldNotDeleteTransactionById() throws DaoException {
        boolean result = dao.deleteById(connection, 1000L);
        Assertions.assertThat(result).isFalse();

    }

    @Test
    void shouldNotDeleteTransaction() throws DaoException {
        AccountTransaction transaction = DataGenerator.generateTransaction();
        transaction.setId(19999L);
        boolean result = dao.delete(connection, transaction);
        Assertions.assertThat(result).isFalse();

    }

    @Test
    void shouldDeleteTransactionThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.deleteById(connection, 1))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldMapTransactionAndThrowSqlException() throws DaoException, SQLException {
        Connection connectionMock = Mockito.mock(Connection.class);
        Statement statementMock = Mockito.mock(Statement.class);
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        Mockito.when(connectionMock.createStatement()).thenReturn(statementMock);
        Mockito.when(statementMock.executeQuery(Mockito.any())).thenReturn(resultSetMock);
        Mockito.when(resultSetMock.next()).thenReturn(true);
        Mockito.when(resultSetMock.getLong(Mockito.any())).thenThrow(SQLException.class);


        Assertions.assertThatException()
                .isThrownBy(() -> dao.findAll(connectionMock))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldFullMapTransactionAndThrowSqlException() throws DaoException, SQLException {
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        Mockito.when(connectionMock.prepareStatement(Mockito.any())).thenReturn(statementMock);
        Mockito.doNothing().when(statementMock).setLong(Mockito.anyInt(), Mockito.anyLong());
        Mockito.when(statementMock.executeQuery()).thenReturn(resultSetMock);
        Mockito.when(resultSetMock.next()).thenReturn(true);
        Mockito.when(resultSetMock.getLong(Mockito.any())).thenThrow(SQLException.class);


        Assertions.assertThatException()
                .isThrownBy(() -> dao.findById(connectionMock, 1L))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldFindAllTransactions() throws DaoException, SQLException {
        List<AccountTransaction> actual = dao.findAll(connection);
        Assertions.assertThat(actual).isNotEmpty().hasSize(80);
    }

    @Test
    void shouldCreateTransaction() throws DaoException, SQLException {
        AccountTransaction generatedTransaction = DataGenerator.generateTransaction();
        generatedTransaction.setTo(Account.builder()
                .id(1L)
                .build());
        generatedTransaction.setFrom(Account.builder()
                .id(2L)
                .build());
        int expected = dao.findAll(connection).size() + 1;
        AccountTransaction transaction = dao.create(connection, generatedTransaction);
        int actual = dao.findAll(connection).size();
        dao.delete(connection, transaction);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldCreateTransactionThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.create(connection, DataGenerator.generateTransaction()))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldUpdateTransactionUnsupportedOperationException() throws DaoException, SQLException {
        Assertions.assertThatException()
                .isThrownBy(() -> dao.update(connection, DataGenerator.generateTransaction()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldFindAllTransactionsByAccount() throws DaoException, SQLException {
        List<AccountTransaction> actual = dao.findAllByAccount(connection, "1111111111");
        Assertions.assertThat(actual).isNotEmpty().hasSize(4);
    }

    @Test
    void shouldFindAllTransactionsByAccountAndThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findAllByAccount(connection, ""))
                .isInstanceOf(DaoException.class);
    }


    @Test
    void shouldFindAllTransactionsByIdAndBetweenDates() throws DaoException, SQLException {
        List<AccountTransaction> actual = dao.findAllByIdAndBetweenDates(connection, 1L,
                LocalDate.of(2023, 8, 14),
                LocalDate.of(2023, 8, 17));
        Assertions.assertThat(actual).isNotEmpty().hasSize(3);
    }

    @Test
    void shouldFindAllTransactionsByIdAndBetweenDatesAndThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findAllByIdAndBetweenDates(connection, 1L, LocalDate.now(), LocalDate.now()))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldFindAllTransactionsByAccountId() throws DaoException, SQLException {
        List<AccountTransaction> actual = dao.findAllByAccountId(connection, 1L);
        Assertions.assertThat(actual).isNotEmpty().hasSize(4);
    }

    @Test
    void shouldFindAllTransactionsByAccountIdAndThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findAllByAccountId(connection, 1L))
                .isInstanceOf(DaoException.class);
    }


}
