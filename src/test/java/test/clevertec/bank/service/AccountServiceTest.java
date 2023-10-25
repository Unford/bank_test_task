package test.clevertec.bank.service;

import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.dao.impl.BankDaoImpl;
import by.clevertec.bank.dao.impl.UserDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.*;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import by.clevertec.bank.util.DataMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;
import test.clevertec.bank.common.DataGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class AccountServiceTest {
    @InjectMocks
    AccountServiceImpl service;

    @Mock
    private AccountTransactionDaoIml transactionDao;
    @Mock
    private AccountDaoImpl accountDao;
    @Mock
    private BankDaoImpl bankDao;
    @Mock
    private UserDaoImpl userDao;
    @Spy
    private ModelMapper modelMapper = DataMapper.getModelMapper();

    @Mock
    private HikariDataSource dataSource;
    @Mock
    private Connection connection;

    @Test
    void shouldFindAllAccountsAndMapToDto() throws ServiceException, SQLException, DaoException {
        List<Account> list = Stream.generate(DataGenerator::generateAccount).limit(3).toList();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findAll(Mockito.any())).thenReturn(list);
        List<AccountDto> expected = list.stream()
                .map(e -> modelMapper.map(e, AccountDto.class))
                .toList();

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);

            List<AccountDto> actual = service.findAll();
            Assertions.assertThat(actual).containsAll(expected);

        }

    }

    @Test
    void shouldFindAccountByIdAndMapToDto() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(account));
        AccountDto expected = modelMapper.map(account, AccountDto.class);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            AccountDto actual = service.findById(1L);
            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }

    @Test
    void shouldNotFindAccountByIdAndThrowException() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.empty());
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);

            Assertions.assertThatException().isThrownBy(() -> service.findById(1L))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);


        }

    }

    @Test
    void shouldDeleteAccountById() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(account));
        Mockito.when(transactionDao.findAllByAccount(Mockito.any(), Mockito.any())).thenReturn(List.of());
        Mockito.when(accountDao.deleteById(Mockito.any(), Mockito.anyLong())).thenReturn(true);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Boolean actual = service.deleteById(1L);
            Assertions.assertThat(actual).isTrue();

        }

    }

    @Test
    void shouldDeleteAccountByIdNotExist() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.empty());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.deleteById(1L))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);

        }

    }

    @Test
    void shouldDeleteAccountByIdButHasTransactions() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()));
        Mockito.when(transactionDao.findAllByAccount(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(DataGenerator.generateTransaction()));

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.deleteById(1L))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.CONFLICT);

        }

    }

    @Test
    void shouldCreateAccount() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateBank()));
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateUser()));
        Mockito.when(accountDao.findByAccountOrBankAndUser(Mockito.any(), Mockito.any(),
                Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(accountDao.create(Mockito.any(), Mockito.any()))
                .thenReturn(account);
        AccountDto expected = modelMapper.map(account, AccountDto.class);
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            AccountDto actual = service.create(DataGenerator.generateAccountDto());
            Assertions.assertThat(actual).isEqualTo(expected);

        }
        Mockito.verify(bankDao, Mockito.times(1)).findById(Mockito.any(), Mockito.anyLong());
        Mockito.verify(userDao, Mockito.times(1)).findById(Mockito.any(), Mockito.anyLong());
        Mockito.verify(accountDao, Mockito.times(1))
                .findByAccountOrBankAndUser(Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(accountDao, Mockito.times(1))
                .create(Mockito.any(), Mockito.any());


    }

    @Test
    void shouldThrowServiceExceptionWhileCreatingAccountUserNotExist() throws ServiceException, SQLException, DaoException {

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateBank()));
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.create(DataGenerator.generateAccountDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);

        }
        Mockito.verify(bankDao, Mockito.times(1)).findById(Mockito.any(), Mockito.anyLong());
        Mockito.verify(userDao, Mockito.times(1)).findById(Mockito.any(), Mockito.anyLong());
        Mockito.verify(accountDao, Mockito.times(0))
                .findByAccountOrBankAndUser(Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.anyLong());

    }


    @Test
    void shouldThrowServiceExceptionWhileCreatingAccountBankNotExist() throws ServiceException, SQLException, DaoException {

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.create(DataGenerator.generateAccountDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);

        }
        Mockito.verify(bankDao, Mockito.times(1)).findById(Mockito.any(), Mockito.anyLong());
        Mockito.verify(userDao, Mockito.times(0)).findById(Mockito.any(), Mockito.anyLong());
        Mockito.verify(accountDao, Mockito.times(0))
                .findByAccountOrBankAndUser(Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.anyLong());


    }


    @Test
    void shouldThrowServiceExceptionWhileCreatingAccountAlreadyExist() throws ServiceException, SQLException, DaoException {

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateBank()));
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateUser()));
        Mockito.when(accountDao.findByAccountOrBankAndUser(Mockito.any(), Mockito.any(),
                Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(DataGenerator.generateAccount()));

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.create(DataGenerator.generateAccountDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.CONFLICT);

        }
        Mockito.verify(bankDao, Mockito.times(1)).findById(Mockito.any(), Mockito.anyLong());
        Mockito.verify(userDao, Mockito.times(1)).findById(Mockito.any(), Mockito.anyLong());
        Mockito.verify(accountDao, Mockito.times(1))
                .findByAccountOrBankAndUser(Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.anyLong());

    }


    @Test
    void shouldUpdateAccount() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(account));
        Mockito.when(accountDao.findByAccountOrBankAndUser(Mockito.any(), Mockito.any(),
                        Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(accountDao.update(Mockito.any(), Mockito.any()))
                .thenReturn(account);

        AccountDto expected = modelMapper.map(account, AccountDto.class);
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            AccountDto actual = service.update(DataGenerator.generateAccountDto());

            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }

    @Test
    void shouldUpdateWithIdNotFoundThrowServiceException() throws ServiceException, SQLException, DaoException {

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.update(DataGenerator.generateAccountDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);


        }

    }

    @Test
    void shouldUpdateButAccountExistThenThrowServiceException() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(account));
        Mockito.when(accountDao.findByAccountOrBankAndUser(Mockito.any(), Mockito.any(),
                        Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(account));

        AccountDto accountDto = modelMapper.map(account, AccountDto.class);
        accountDto.setId(account.getId() - 1);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.update(accountDto))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.CONFLICT);


        }

    }

    @Test
    void shouldUpdateButAccountEqualsFormer() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(account));
        Mockito.when(accountDao.findByAccountOrBankAndUser(Mockito.any(), Mockito.any(),
                        Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(account));
        Mockito.when(accountDao.update(Mockito.any(), Mockito.any()))
                .thenReturn(account);
        AccountDto expected = modelMapper.map(account, AccountDto.class);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            AccountDto actual = service.update(expected);
            Assertions.assertThat(actual).isEqualTo(expected);


        }

    }


    @Test
    void shouldGetAccountBalance() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        BigDecimal expected = BigDecimal.TEN;

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(account));
        Mockito.when(accountDao.sumAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(expected);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            BigDecimal actual = service.getAccountBalance(account.getId());
            Assertions.assertThat(actual).isEqualTo(expected);


        }

    }

    @Test
    void shouldGetAccountBalanceButAccountNotExist() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.getAccountBalance(1L))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);


        }

    }

    @Test
    void shouldGetAccountStatement() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        MoneyStatsDto moneyStatsDto = DataGenerator.generateMoneyStatsDto();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(account));
        Mockito.when(accountDao.calculateMoneyDataAllByIdAndBetweenDates(Mockito.any(),
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(moneyStatsDto);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            AccountStatementDto actual = service.getAccountStatement(DataGenerator.generateAccountStatementDto());
            Assertions.assertThat(actual.getAccount()).isEqualTo(modelMapper.map(account, AccountDto.class));
            Assertions.assertThat(actual.getMoney()).isEqualTo(moneyStatsDto);
        }

    }

    @Test
    void shouldGetAccountStatementAccountNotFound() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.getAccountStatement(DataGenerator.generateAccountStatementDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);
        }

    }


    @Test
    void shouldGetAccountExtractAccountNotFound() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.getAccountExtract(DataGenerator.generateAccountExtractDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);
        }

    }

    @Test
    void shouldGetAccountExtract() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        BigDecimal sum = BigDecimal.TEN;
        List<AccountTransaction> accountTransactions = List.of(DataGenerator.generateTransaction(), DataGenerator.generateTransaction());
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(account));
        Mockito.when(accountDao.sumAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(sum);
        Mockito.when(transactionDao.findAllByIdAndBetweenDates(Mockito.any(),
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(accountTransactions);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            AccountExtractDto actual = service.getAccountExtract(DataGenerator.generateAccountExtractDto());
            Assertions.assertThat(actual.getAccount()).isEqualTo(modelMapper.map(account, AccountDto.class));
            Assertions.assertThat(actual.getBalance()).isEqualTo(sum);
            Assertions.assertThat(actual.getTransactions()).containsAll(accountTransactions.stream()
                    .map(a->modelMapper.map(a, TransactionDto.class)).toList());

        }

    }


    @Test
    void shouldAccrueIncome() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        BigDecimal sum = BigDecimal.TEN;
        List<AccountTransaction> accountTransactions = List.of(DataGenerator.generateTransaction(), DataGenerator.generateTransaction());
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findAllAccrual(Mockito.any()))
                .thenReturn(List.of(DataGenerator.generateAccount(), DataGenerator.generateAccount()));
        Mockito.when(accountDao.sumAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(sum);
        Mockito.when(transactionDao.create(Mockito.any(), Mockito.any()))
                .thenReturn(DataGenerator.generateTransaction());
        Mockito.when(accountDao.updateLastAccrualDate(Mockito.any(), Mockito.any()))
                .thenReturn(null);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            service.accrueIncome(5);


        }

        Mockito.verify(accountDao, Mockito.times(1))
                .findAllAccrual(Mockito.any());
        Mockito.verify(accountDao, Mockito.times(accountTransactions.size()))
                .sumAllByAccountId(Mockito.any(), Mockito.anyLong());
        Mockito.verify(transactionDao, Mockito.times(accountTransactions.size()))
                .create(Mockito.any(), Mockito.any());

    }

    @Test
    void shouldAccrueIncomeWithoutMoney() throws ServiceException, SQLException, DaoException {
        Account account = DataGenerator.generateAccount();
        BigDecimal sum = BigDecimal.TEN;
        List<AccountTransaction> accountTransactions = List.of(DataGenerator.generateTransaction(), DataGenerator.generateTransaction());
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findAllAccrual(Mockito.any()))
                .thenReturn(List.of(DataGenerator.generateAccount(), DataGenerator.generateAccount()));
        Mockito.when(accountDao.sumAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(sum, BigDecimal.ZERO);
        Mockito.when(transactionDao.create(Mockito.any(), Mockito.any()))
                .thenReturn(DataGenerator.generateTransaction());
        Mockito.when(accountDao.updateLastAccrualDate(Mockito.any(), Mockito.any()))
                .thenReturn(null);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            service.accrueIncome(5);


        }

        Mockito.verify(accountDao, Mockito.times(1))
                .findAllAccrual(Mockito.any());
        Mockito.verify(accountDao, Mockito.times(accountTransactions.size()))
                .sumAllByAccountId(Mockito.any(), Mockito.anyLong());
        Mockito.verify(transactionDao, Mockito.times(1))
                .create(Mockito.any(), Mockito.any());

    }


}
