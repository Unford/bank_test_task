package test.clevertec.bank.dao;

import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.domain.User;
import by.clevertec.bank.model.dto.MoneyStatsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import test.clevertec.bank.gen.DataGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class AccountDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.9")
            .withInitScript("init_script.sql");
    Connection connection;
    AccountDaoImpl dao = new AccountDaoImpl();

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
    void shouldFindAccount() throws DaoException {
        Account expected = Account.builder()
                .id(1L)
                .account("1111111111")
                .openDate(LocalDate.now())
                .lastAccrualDate(LocalDate.now())
                .bank(new Bank(1L, "Bank A"))
                .user(new User(1L, "John Doe"))
                .build();
        Optional<Account> actual = dao.findById(connection, expected.getId());
        Assertions.assertThat(actual)
                .isPresent().get().isEqualTo(expected);
    }

    @Test
    void shouldNotFindAccount() throws DaoException {
        Optional<Account> actual = dao.findById(connection, 1000L);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void shouldNotFindAccountThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findById(connection, 1))
                .isInstanceOf(DaoException.class);
    }


    @Test
    void shouldDeleteAccountById() throws DaoException {
        Account generated = DataGenerator.generateAccount();
        generated.setBank(Bank.builder()
                .id(1L)
                .build());
        generated.setUser(User.builder()
                .id(1L)
                .build());

        Account account = dao.create(connection, generated);
        int expected = dao.findAll(connection).size() - 1;

        boolean result = dao.deleteById(connection, account.getId());
        Assertions.assertThat(result).isTrue();
        int actual = dao.findAll(connection).size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldDeleteAccount() throws DaoException {
        Account generated = DataGenerator.generateAccount();
        generated.setBank(Bank.builder()
                .id(1L)
                .build());
        generated.setUser(User.builder()
                .id(1L)
                .build());
        Account account = dao.create(connection, generated);
        int expected = dao.findAll(connection).size() - 1;

        boolean result = dao.delete(connection, account);
        Assertions.assertThat(result).isTrue();
        int actual = dao.findAll(connection).size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldNotDeleteAccountById() throws DaoException {
        boolean result = dao.deleteById(connection, 1000L);
        Assertions.assertThat(result).isFalse();

    }

    @Test
    void shouldNotDeleteAccount() throws DaoException {
        Account account = DataGenerator.generateAccount();
        account.setId(19999L);
        boolean result = dao.delete(connection, account);
        Assertions.assertThat(result).isFalse();

    }

    @Test
    void shouldDeleteAccountThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.deleteById(connection, 1))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldMapAccountAndThrowSqlException() throws DaoException, SQLException {
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
    void shouldFullMapAccountAndThrowSqlException() throws DaoException, SQLException {
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
    void shouldFindAllAccounts() throws DaoException, SQLException {
        List<Account> actual = dao.findAll(connection);
        Assertions.assertThat(actual).isNotEmpty().hasSize(40);
    }

    @Test
    void shouldCreateAccount() throws DaoException, SQLException {
        Account generated = DataGenerator.generateAccount();
        generated.setBank(Bank.builder()
                .id(1L)
                .build());
        generated.setUser(User.builder()
                .id(1L)
                .build());
        int expected = dao.findAll(connection).size() + 1;
        Account account = dao.create(connection, generated);
        int actual = dao.findAll(connection).size();
        dao.delete(connection, account);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldCreateAccountThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.create(connection, DataGenerator.generateAccount()))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldUpdateAccount() throws DaoException, SQLException {
        Account generated = DataGenerator.generateAccount();
        generated.setBank(Bank.builder()
                .id(2L)
                .build());
        generated.setUser(User.builder()
                .id(2L)
                .build());
        Account expected = dao.create(connection, generated);
        expected.setAccount("some account text");
        dao.update(connection, expected);
        Account actual = dao.findById(connection, expected.getId()).get();
        Assertions.assertThat(actual).isEqualTo(expected);
        dao.delete(connection, expected);
    }

    @Test
    void shouldUpdateAccountThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.update(connection, DataGenerator.generateAccount()))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldSumAllByAccountIdAccount() throws DaoException, SQLException {
        BigDecimal expected = BigDecimal.valueOf(65.0).setScale(2);
        BigDecimal actual = dao.sumAllByAccountId(connection, 1L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldSumAllByAccountIdAccountNotExist() throws DaoException, SQLException {
        BigDecimal expected = BigDecimal.ZERO;
        BigDecimal actual = dao.sumAllByAccountId(connection, 1000L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldSumAllByAccountIdThrowDaoException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.sumAllByAccountId(connection, 1000L))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldSumAllByAccountIdAndGetResultSetNextFalse() throws DaoException, SQLException {
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        Mockito.when(connectionMock.prepareStatement(Mockito.any())).thenReturn(statementMock);
        Mockito.doNothing().when(statementMock).setLong(Mockito.anyInt(), Mockito.anyLong());
        Mockito.when(statementMock.executeQuery()).thenReturn(resultSetMock);
        Mockito.when(resultSetMock.next()).thenReturn(false);

        BigDecimal expected = BigDecimal.ZERO;
        BigDecimal actual = dao.sumAllByAccountId(connectionMock, 1L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldFindAllAccrual() throws DaoException, SQLException {

        List<Account> actual = dao.findAllAccrual(connection);
        Assertions.assertThat(actual).extracting(Account::getLastAccrualDate)
                .isNotEmpty()
                .allSatisfy(date -> {
                    Assertions.assertThat(date).isBefore(LocalDate.now());
                });
    }

    @Test
    void shouldFindAllAccrualAndThrowDaoException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findAllAccrual(connection))
                .isInstanceOf(DaoException.class);

    }

    @Test
    void shouldUpdateAccountAccrualDate() throws DaoException, SQLException {
        Account account = dao.findById(connection, 16L).get();
        LocalDate expected = account.getLastAccrualDate();

        dao.updateLastAccrualDate(connection, account);
        Account actual = dao.findById(connection, 16L).get();

        Assertions.assertThat(actual.getLastAccrualDate()).isAfter(expected);

    }

    @Test
    void shouldUpdateAccountAccrualDateThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.updateLastAccrualDate(connection, DataGenerator.generateAccount()))
                .isInstanceOf(DaoException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5})
    void shouldFindAllByUserId(long id) throws DaoException, SQLException {
        List<Account> actual = dao.findAllByUserId(connection, id);

        Assertions.assertThat(actual)
                .extracting(Account::getUser)
                .extracting(User::getId)
                .containsOnly(id);

    }

    @Test
    void shouldFindAllByUserIdThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findAllByUserId(connection, 1L))
                .isInstanceOf(DaoException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5})
    void shouldFindAllByBankId(long id) throws DaoException, SQLException {
        List<Account> actual = dao.findAllByBankId(connection, id);

        Assertions.assertThat(actual)
                .extracting(Account::getBank)
                .extracting(Bank::getId)
                .containsOnly(id);

    }

    @Test
    void shouldFindAllByBankIdThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findAllByBankId(connection, 1L))
                .isInstanceOf(DaoException.class);
    }


    @Test
    void shouldFindByAccount() throws DaoException {
        Account expected = Account.builder()
                .id(1L)
                .account("1111111111")
                .openDate(LocalDate.now())
                .lastAccrualDate(LocalDate.now())
                .bank(new Bank(1L, "Bank A"))
                .user(new User(1L, "John Doe"))
                .build();
        Optional<Account> actual = dao.findByAccountOrBankAndUser(connection, "1111111111", 2L, 2L);
        Assertions.assertThat(actual)
                .isPresent().get().isEqualTo(expected);
    }

    @Test
    void shouldFindByAccountOrBankAndUser() throws DaoException {
        Account expected = Account.builder()
                .id(1L)
                .account("1111111111")
                .openDate(LocalDate.now())
                .lastAccrualDate(LocalDate.now())
                .bank(new Bank(1L, "Bank A"))
                .user(new User(1L, "John Doe"))
                .build();
        Optional<Account> actual = dao.findByAccountOrBankAndUser(connection, "1", 1L, 1L);
        Assertions.assertThat(actual)
                .isPresent().get().isEqualTo(expected);
    }

    @Test
    void shouldFindByAccountOrBankAndUserResultSetNextFalse() throws DaoException, SQLException {
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        Mockito.when(connectionMock.prepareStatement(Mockito.any())).thenReturn(statementMock);
        Mockito.doNothing().when(statementMock).setString(Mockito.anyInt(), Mockito.any());
        Mockito.doNothing().when(statementMock).setLong(Mockito.anyInt(), Mockito.anyLong());
        Mockito.when(statementMock.executeQuery()).thenReturn(resultSetMock);
        Mockito.when(resultSetMock.next()).thenReturn(false);


        Optional<Account> actual = dao.findByAccountOrBankAndUser(connectionMock, "1L", 1L, 1L);
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindByAccountOrBankAndUserThrowSqlException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.findByAccountOrBankAndUser(connection, "1", 1L, 1L))
                .isInstanceOf(DaoException.class);
    }

    @Test
    void shouldCalculateMoneyDataAllByIdAndBetweenDates() throws DaoException {
        MoneyStatsDto expected = MoneyStatsDto.builder()
                .income(BigDecimal.valueOf(300.0).setScale(2))
                .expenditure(BigDecimal.valueOf(-50.0).setScale(2))
                .balance(BigDecimal.valueOf(250.0).setScale(2))
                .build();
        MoneyStatsDto actual = dao.calculateMoneyDataAllByIdAndBetweenDates(connection, 4L,
                LocalDate.of(2023, 8, 15),
                LocalDate.of(2023, 8, 17));
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldCalculateMoneyDataAllByIdAndBetweenDatesAndThrowDaoException() throws DaoException, SQLException {
        connection.close();
        Assertions.assertThatException()
                .isThrownBy(() -> dao.calculateMoneyDataAllByIdAndBetweenDates(connection, 4L,
                        LocalDate.of(2023, 8, 15),
                        LocalDate.of(2023, 8, 17)))
                .isInstanceOf(DaoException.class);


    }

    @Test
    void shouldCalculateMoneyDataAllByIdAndBetweenDatesResultSetNextFalse() throws DaoException, SQLException {
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        Mockito.when(connectionMock.prepareStatement(Mockito.any())).thenReturn(statementMock);
        Mockito.doNothing().when(statementMock).setLong(Mockito.anyInt(), Mockito.anyLong());
        Mockito.doNothing().when(statementMock).setDate(Mockito.anyInt(), Mockito.any());

        Mockito.when(statementMock.executeQuery()).thenReturn(resultSetMock);
        Mockito.when(resultSetMock.next()).thenReturn(false);


        MoneyStatsDto actual = dao.calculateMoneyDataAllByIdAndBetweenDates(connectionMock, 4L,
                LocalDate.of(2023, 8, 15),
                LocalDate.of(2023, 8, 17));
        Assertions.assertThat(actual).isEqualTo(new MoneyStatsDto());
    }

}
