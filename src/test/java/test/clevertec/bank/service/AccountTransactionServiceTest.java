package test.clevertec.bank.service;

import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import by.clevertec.bank.util.DataMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import test.clevertec.bank.gen.DataGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
public class AccountTransactionServiceTest {
    @InjectMocks
    AccountTransactionServiceImpl service;

    @Mock
    private AccountTransactionDaoIml transactionDao;
    @Mock
    private AccountDaoImpl accountDao;

    @Spy
    private ModelMapper modelMapper = DataMapper.getModelMapper();

    @Mock
    private HikariDataSource dataSource;
    @Mock
    private Connection connection;

    @Test
    void shouldThrowUnsupportedOperationExceptionWhenUpdate() throws ServiceException {
        Assertions.assertThatException()
                .isThrownBy(() -> service.update(DataGenerator.generateTransactionDto()))
                .isExactlyInstanceOf(UnsupportedOperationException.class)
                .withMessageContaining("unsupported");

    }

    @Test
    void shouldThrowUnsupportedOperationExceptionWhenCreate() throws ServiceException {

        Assertions.assertThatException()
                .isThrownBy(() -> service.create(DataGenerator.generateTransactionDto()))
                .isExactlyInstanceOf(UnsupportedOperationException.class)
                .withMessageContaining("unsupported");
    }

    @Test
    void shouldDFindAllTransaction() throws ServiceException, SQLException, DaoException {
        List<AccountTransaction> transactions =
                Stream.generate(DataGenerator::generateTransaction).limit(3).toList();

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(transactionDao.findAll(Mockito.any())).thenReturn(transactions);
        List<TransactionDto> expected = transactions.stream()
                .map(e -> modelMapper.map(e, TransactionDto.class))
                .toList();
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            List<TransactionDto> actual = service.findAll();
            Assertions.assertThat(actual).containsAll(expected);
        }

    }

    @Test
    void shouldDFindAllTransactionByAccount() throws ServiceException, SQLException, DaoException {
        List<AccountTransaction> transactions =
                Stream.generate(DataGenerator::generateTransaction).limit(3).toList();

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(transactionDao.findAllByAccount(Mockito.any(), Mockito.any()))
                .thenReturn(transactions);
        List<TransactionDto> expected = transactions.stream()
                .map(e -> modelMapper.map(e, TransactionDto.class))
                .toList();
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            List<TransactionDto> actual = service.findAllByAccount("account");
            Assertions.assertThat(actual).containsAll(expected);
        }

    }

    @Test
    void shouldDFindAllTransactionByAccountId() throws ServiceException, SQLException, DaoException {
        List<AccountTransaction> transactions =
                Stream.generate(DataGenerator::generateTransaction).limit(3).toList();

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(transactionDao.findAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(transactions);

        List<TransactionDto> expected = transactions.stream()
                .map(e -> modelMapper.map(e, TransactionDto.class))
                .toList();

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            List<TransactionDto> actual = service.findAllByAccountId(1L);
            Assertions.assertThat(actual).containsAll(expected);
        }

    }

    @Test
    void shouldDFindTransactionById() throws ServiceException, SQLException, DaoException {
        AccountTransaction transaction = DataGenerator.generateTransaction();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(transactionDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(transaction));

        TransactionDto expected = modelMapper.map(transaction, TransactionDto.class);
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            TransactionDto actual = service.findById(1L);
            Assertions.assertThat(actual).isEqualTo(expected);
        }

    }

    @Test
    void shouldDNotFindTransactionByIdAndThrowServiceException() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(transactionDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

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
    void shouldDeleteTransactionById() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(transactionDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateTransaction()));
        Mockito.when(transactionDao.deleteById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(true);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            boolean actual = service.deleteById(1);
            Assertions.assertThat(actual).isTrue();
        }

    }

    @Test
    void shouldDeleteTransactionByIdNutNotFound() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(transactionDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.deleteById(1L))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);
        }
        Mockito.verify(transactionDao, Mockito.times(0))
                .deleteById(Mockito.any(), Mockito.anyLong());
    }

    @Test
    void shouldDepositTransaction() throws ServiceException, SQLException, DaoException {
        AccountTransaction transaction = DataGenerator.generateTransaction();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()));
        Mockito.when(transactionDao.create(Mockito.any(), Mockito.any()))
                .thenReturn(transaction);

        TransactionDto expected = modelMapper.map(transaction, TransactionDto.class);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            TransactionDto actual = service.deposit(DataGenerator.generateTransactionDto());
            Assertions.assertThat(actual).isEqualTo(expected);
        }

    }

    @Test
    void shouldDepositTransactionAccountNotFound() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());



        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.deposit(DataGenerator.generateTransactionDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);

        }

    }

    @Test
    void shouldCreateWithdrawalTransaction() throws ServiceException, SQLException, DaoException {
        AccountTransaction transaction = DataGenerator.generateTransaction();
        transaction.setSum(BigDecimal.TEN);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()));
        Mockito.when(accountDao.sumAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(BigDecimal.valueOf(100));

        Mockito.when(transactionDao.create(Mockito.any(), Mockito.any()))
                .thenReturn(transaction);
        TransactionDto expected = modelMapper.map(transaction, TransactionDto.class);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            TransactionDto actual = service.withdrawal(modelMapper.map(transaction, TransactionDto.class));
            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }




    @Test
    void shouldWithdrawalTransactionAccountNotFound() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.withdrawal(DataGenerator.generateTransactionDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);

        }

    }

    @Test
    void shouldCreateWithdrawalTransactionNotEnoughMoney() throws ServiceException, SQLException, DaoException {
        AccountTransaction transaction = DataGenerator.generateTransaction();
        transaction.setSum(BigDecimal.valueOf(100));
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()));
        Mockito.when(accountDao.sumAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(BigDecimal.ONE);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.withdrawal(modelMapper.map(transaction, TransactionDto.class)))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.CONFLICT);

        }

    }


    @Test
    void shouldTransferMoneyTransactionFromAccountNotFound() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.transferMoney(DataGenerator.generateTransactionDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);

        }
        Mockito.verify(accountDao, Mockito.times(1))
                .findById(Mockito.any(), Mockito.anyLong());

    }

    @Test
    void shouldTransferMoneyTransactionToAccountNotFound() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()))
                .thenReturn(Optional.empty());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.transferMoney(DataGenerator.generateTransactionDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);

        }
        Mockito.verify(accountDao, Mockito.times(2))
                .findById(Mockito.any(), Mockito.anyLong());

    }

    @Test
    void shouldTransferMoneyTransactionNotEnoughMoney() throws ServiceException, SQLException, DaoException {
        TransactionDto dto = DataGenerator.generateTransactionDto();
        dto.setSum(BigDecimal.TEN);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()));
        Mockito.when(accountDao.sumAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(BigDecimal.ONE);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.transferMoney(DataGenerator.generateTransactionDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.CONFLICT);

        }
        Mockito.verify(accountDao, Mockito.times(2))
                .findById(Mockito.any(), Mockito.anyLong());

    }

    @Test
    void shouldTransferMoneyTransaction() throws ServiceException, SQLException, DaoException {
        TransactionDto expected = DataGenerator.generateTransactionDto();
        expected.setSum(BigDecimal.ONE);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(accountDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()))
                .thenReturn(Optional.of(DataGenerator.generateAccount()));
        Mockito.when(accountDao.sumAllByAccountId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(BigDecimal.TEN);
        AccountTransaction res = modelMapper.map(expected, AccountTransaction.class);
        Mockito.when(transactionDao.create(Mockito.any(), Mockito.any())).thenReturn(res);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            TransactionDto actual = service.transferMoney(expected);
            Assertions.assertThat(actual).isEqualTo(expected);

        }


    }

}
