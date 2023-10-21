package test.clevertec.bank.dao;

import by.clevertec.bank.dao.impl.UserDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import test.clevertec.bank.gen.DataGenerator;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.9")
            .withInitScript("init_script.sql");
    Connection connection;
    @Spy
    UserDaoImpl userDao = new UserDaoImpl();


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
    void shouldFindUser() throws DaoException {
        User expected = new User(1L, "John Doe");
        Optional<User> actual = userDao.findById(connection, 1L);
        Assertions.assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @Test
    void shouldNotFindUser() throws DaoException {
        Optional<User> actual = userDao.findById(connection, 1000L);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void shouldNotFindUserThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> userDao.findById(connection, 1))
                .isInstanceOf(DaoException.class);
    }


    @Test
    void shouldDeleteUserById() throws DaoException {
        User user = userDao.create(connection, new User(null, "super unique name"));
        int expected = userDao.findAll(connection).size() - 1;

        boolean result = userDao.deleteById(connection, user.getId());
        Assertions.assertThat(result).isTrue();
        int actual = userDao.findAll(connection).size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldDeleteUser() throws DaoException {
        User user = userDao.create(connection, new User(null, "super unique name"));
        int expected = userDao.findAll(connection).size() - 1;

        boolean result = userDao.delete(connection, user);
        Assertions.assertThat(result).isTrue();
        int actual = userDao.findAll(connection).size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldNotDeleteUserById() throws DaoException {

        boolean result = userDao.deleteById(connection, 1000L);
        Assertions.assertThat(result).isFalse();

    }

    @Test
    void shouldNotDeleteUser() throws DaoException {
        boolean result = userDao.delete(connection, new User(1000L, "1"));
        Assertions.assertThat(result).isFalse();

    }

    @Test
    void shouldDeleteUserThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> userDao.deleteById(connection, 1))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldMapUserAndThrowSqlException() throws DaoException, SQLException {
        Connection connectionMock = Mockito.mock(Connection.class);
        Statement statementMock = Mockito.mock(Statement.class);
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        Mockito.when(connectionMock.createStatement()).thenReturn(statementMock);
        Mockito.when(statementMock.executeQuery(Mockito.any())).thenReturn(resultSetMock);
        Mockito.when(resultSetMock.next()).thenReturn(true);
        Mockito.when(resultSetMock.getLong(Mockito.any())).thenThrow(SQLException.class);


        Assertions.assertThatException()
                .isThrownBy(() -> userDao.findAll(connectionMock))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldFindAllUsers() throws DaoException, SQLException {
        List<User> actual = userDao.findAll(connection);
        Assertions.assertThat(actual).isNotEmpty().hasSize(20);
    }

    @Test
    void shouldCreateUser() throws DaoException, SQLException {
        int expected = userDao.findAll(connection).size() + 1;
        User user = userDao.create(connection, new User(null, "super unique name"));
        int actual = userDao.findAll(connection).size();
        userDao.delete(connection, user);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldCreateUserThrowSqlException() throws DaoException, SQLException {

        Assertions.assertThatException()
                .isThrownBy(() -> userDao.create(connection, new User()))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldUpdateUserThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> userDao.update(connection, new User()))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldUpdateUser() throws DaoException, SQLException {
        User expected = userDao.findById(connection, 1L).get();
        expected.setFullName("Updated name");
        userDao.update(connection, expected);
        User actual = userDao.findById(connection, 1L).get();
        Assertions.assertThat(actual).isEqualTo(expected);
    }


    @Test
    void shouldCreateUserAndNotGetIdBack() throws DaoException, SQLException {
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        Mockito.when(connectionMock.prepareStatement(Mockito.any(), Mockito.anyInt())).thenReturn(statementMock);

        Mockito.when(statementMock.executeUpdate()).thenReturn(1);
        Mockito.when(statementMock.getGeneratedKeys()).thenReturn(resultSetMock);
        Mockito.when(resultSetMock.next()).thenReturn(false);
        Mockito.doReturn(Optional.of(new User(1L, "test")))
                .when(userDao).findById(Mockito.any(), Mockito.anyLong());

        User user = DataGenerator.generateUser();
        User actual = userDao.create(connectionMock, user);

        Assertions.assertThat(actual.getId()).isEqualTo(1);
        Assertions.assertThat(user.getId()).isEqualTo(1);
    }

    @Test
    void shouldCreateUserAndThrowDaoExceptionInsidePerform() throws DaoException, SQLException {
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);

        Mockito.when(connectionMock.prepareStatement(Mockito.any(), Mockito.anyInt())).thenReturn(statementMock);

        Mockito.when(statementMock.executeUpdate()).thenThrow(new SQLException());


        Assertions.assertThatException()
                .isThrownBy(() -> userDao.create(connectionMock, new User()))
                .isInstanceOf(DaoException.class);
    }

}
