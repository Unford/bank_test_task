package test.clevertec.bank.service;

import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.BankDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.dto.BankDto;
import by.clevertec.bank.service.impl.BankServiceImpl;
import by.clevertec.bank.util.DataMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;
import test.clevertec.bank.common.DataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class BankServiceTest {
    @InjectMocks
    BankServiceImpl bankService;
    @Mock
    private AccountDaoImpl accountDao;
    @Mock
    private BankDaoImpl bankDao;

    @Mock
    private HikariDataSource dataSource;
    @Mock
    private Connection connection;
    @Spy
    private ModelMapper modelMapper = DataMapper.getModelMapper();

    @Test
    void shouldFindAllBanksAndMapToDto() throws ServiceException, SQLException, DaoException {
        List<Bank> banks = Stream.generate(DataGenerator::generateBank).limit(3).toList();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findAll(Mockito.any())).thenReturn(banks);
        List<BankDto> expected = banks.stream()
                .map(e -> modelMapper.map(e, BankDto.class))
                .toList();
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            List<BankDto> actual = bankService.findAll();
            Assertions.assertThat(actual).containsAll(expected);

        }

    }

    @Test
    void shouldFindBankByIdAndMapToDto() throws ServiceException, SQLException, DaoException {
        Bank bank = DataGenerator.generateBank();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(bank));
        BankDto expected = modelMapper.map(bank, BankDto.class);
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            BankDto actual = bankService.findById(1L);
            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }

    @Test
    void shouldNotFindBankByIdAndThrowException() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.empty());
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);

            Assertions.assertThatException().isThrownBy(() -> bankService.findById(1L))
                    .isInstanceOf(ServiceException.class)
                    .withMessageContaining("is not found");


        }

    }

    @Test
    void shouldDeleteBankById() throws ServiceException, SQLException, DaoException {
        Bank bank = DataGenerator.generateBank();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(bank));
        Mockito.when(accountDao.findAllByBankId(Mockito.any(), Mockito.anyLong())).thenReturn(List.of());
        Mockito.when(bankDao.deleteById(Mockito.any(), Mockito.anyLong())).thenReturn(true);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Boolean actual = bankService.deleteById(1L);
            Assertions.assertThat(actual).isTrue();

        }

    }

    @Test
    void shouldDeleteBankByIdNotExist() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.empty());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> bankService.deleteById(1L))
                    .isInstanceOf(ServiceException.class);

        }

    }

    @Test
    void shouldDeleteBankByIdButBankHasAccounts() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateBank()));
        Mockito.when(accountDao.findAllByBankId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(List.of(DataGenerator.generateAccount()));

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> bankService.deleteById(1L))
                    .isInstanceOf(ServiceException.class);

        }

    }

    @Test
    void shouldCreateBank() throws ServiceException, SQLException, DaoException {
        Bank bank = DataGenerator.generateBank();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findByName(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(bankDao.create(Mockito.any(), Mockito.any()))
                .thenReturn(bank);
        BankDto expected = modelMapper.map(bank, BankDto.class);
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            BankDto actual = bankService.create(DataGenerator.generateBankDto());

            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }

    @Test
    void shouldCreateBankButBankNameAlreadyUsed() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findByName(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(DataGenerator.generateBank()));

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);


            Assertions.assertThatException()
                    .isThrownBy(() -> bankService.create(DataGenerator.generateBankDto()))
                    .isInstanceOf(ServiceException.class);

        }

    }

    @Test
    void shouldUpdateBank() throws ServiceException, SQLException, DaoException {
        Bank bank = DataGenerator.generateBank();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(bank));
        Mockito.when(bankDao.findByName(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(bankDao.update(Mockito.any(), Mockito.any()))
                .thenReturn(bank);

        BankDto expected = modelMapper.map(bank, BankDto.class);
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            BankDto actual = bankService.update(DataGenerator.generateBankDto());

            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }

    @Test
    void shouldUpdateWithIdNotFoundThrowServiceException() throws ServiceException, SQLException, DaoException {

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> bankService.update(DataGenerator.generateBankDto()))
                    .isInstanceOf(ServiceException.class);


        }

    }

    @Test
    void shouldUpdateBankButNameUsed() throws ServiceException, SQLException, DaoException {
        Bank bank = DataGenerator.generateBank();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(bank));
        Mockito.when(bankDao.findByName(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(bank));
        BankDto bankDto = DataGenerator.generateBankDto();
        bankDto.setId(bankDto.getId() - 1);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> bankService.update(bankDto))
                    .isInstanceOf(ServiceException.class);

        }

    }

    @Test
    void shouldUpdateBankButNameEqualsFormer() throws ServiceException, SQLException, DaoException {
        Bank bank = DataGenerator.generateBank();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(bankDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(bank));
        Mockito.when(bankDao.findByName(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(bank));
        Mockito.when(bankDao.update(Mockito.any(), Mockito.any()))
                .thenReturn(bank);
        BankDto expected = modelMapper.map(bank, BankDto.class);


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            BankDto actual = bankService.update(expected);
            Assertions.assertThat(actual).isEqualTo(expected);
        }

    }
}
