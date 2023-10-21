package test.clevertec.bank.dao;

import by.clevertec.bank.dao.impl.BankDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Bank;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.util.List;
import java.util.Optional;

class BankDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.9")
            .withInitScript("init_script.sql");
    Connection connection;
    BankDaoImpl dao = new BankDaoImpl();

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
    void shouldFindBank() throws DaoException {
        Bank expected = new Bank(1L, "Bank A");
        Optional<Bank> actual = dao.findById(connection, expected.getId());
        Assertions.assertThat(actual)
                .isPresent().get().isEqualTo(expected);
    }

    @Test
    void shouldNotFindBank() throws DaoException {
        Optional<Bank> actual = dao.findById(connection, 1000L);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void shouldNotFindBankThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findById(connection, 1))
                .isInstanceOf(DaoException.class);
    }


    @Test
    void shouldDeleteBankById() throws DaoException {
        Bank bank = dao.create(connection, new Bank(null, "super unique name"));
        int expected = dao.findAll(connection).size() - 1;

        boolean result = dao.deleteById(connection, bank.getId());
        Assertions.assertThat(result).isTrue();
        int actual = dao.findAll(connection).size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldDeleteBank() throws DaoException {
        Bank bank = dao.create(connection, new Bank(null, "super unique name"));
        int expected = dao.findAll(connection).size() - 1;

        boolean result = dao.delete(connection, bank);
        Assertions.assertThat(result).isTrue();
        int actual = dao.findAll(connection).size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldNotDeleteBankById() throws DaoException {
        boolean result = dao.deleteById(connection, 1000L);
        Assertions.assertThat(result).isFalse();

    }

    @Test
    void shouldNotDeleteBank() throws DaoException {
        boolean result = dao.delete(connection, new Bank(1000L, "1"));
        Assertions.assertThat(result).isFalse();

    }

    @Test
    void shouldDeleteBankThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.deleteById(connection, 1))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldMapBankAndThrowSqlException() throws DaoException, SQLException {
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
    void shouldFindAllBanks() throws DaoException, SQLException {
        List<Bank> actual = dao.findAll(connection);
        Assertions.assertThat(actual).isNotEmpty().hasSize(5);
    }

    @Test
    void shouldCreateBank() throws DaoException, SQLException {
        int expected = dao.findAll(connection).size() + 1;
        Bank user = dao.create(connection, new Bank(null, "super unique name"));
        int actual = dao.findAll(connection).size();
        dao.delete(connection, user);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldCreateBankThrowSqlException() throws DaoException, SQLException {

        Assertions.assertThatException()
                .isThrownBy(() -> dao.create(connection, new Bank()))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldUpdateBankThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.update(connection, new Bank()))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldUpdateBank() throws DaoException, SQLException {
        Bank expected = dao.findById(connection, 1L).get();
        expected.setName("Updated name");
        dao.update(connection, expected);
        Bank actual = dao.findById(connection, 1L).get();
        Assertions.assertThat(actual).isEqualTo(expected);
        dao.update(connection, new Bank(1L, "Bank A"));
    }

    @Test
    void shouldFindBankByName() throws DaoException, SQLException {
        Bank expected = new Bank(1L, "Bank A");
        Bank actual = dao.findByName(connection, "Bank A").get();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldFindBankByNameAndThrowDaoException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findByName(connection, "name"))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldNotFindBankByName() throws DaoException {
        Optional<Bank> actual = dao.findByName(connection, "1000L");
        Assertions.assertThat(actual).isEmpty();
    }
}
