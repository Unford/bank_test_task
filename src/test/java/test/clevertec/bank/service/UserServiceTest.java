package test.clevertec.bank.service;

import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.UserDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.User;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.model.dto.UserDto;
import by.clevertec.bank.service.impl.UserServiceImpl;
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl service;
    @Mock
    private AccountDaoImpl accountDao;
    @Mock
    private UserDaoImpl userDao;
    @Spy
    private ModelMapper modelMapper = DataMapper.getModelMapper();

    @Mock
    private HikariDataSource dataSource;
    @Mock
    private Connection connection;

    @Test
    void shouldFindAllUsersAndMapToDto() throws ServiceException, SQLException, DaoException {
        List<User> userList = Stream.generate(DataGenerator::generateUser).limit(3).toList();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.findAll(Mockito.any())).thenReturn(userList);
        List<UserDto> expected = userList.stream()
                .map(e -> modelMapper.map(e, UserDto.class))
                .toList();

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);

            List<UserDto> actual = service.findAll();
            Assertions.assertThat(actual).containsAll(expected);

        }

    }

    @Test
    void shouldFindUserByIdAndMapToDto() throws ServiceException, SQLException, DaoException {
        User user = DataGenerator.generateUser();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(user));
        UserDto expected = modelMapper.map(user, UserDto.class);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            UserDto actual = service.findById(1L);
            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }

    @Test
    void shouldNotFindUserByIdAndThrowException() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.empty());
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);

            Assertions.assertThatException().isThrownBy(() -> service.findById(1L))
                    .isInstanceOf(ServiceException.class)
                    .withMessageContaining("is not found");


        }

    }

    @Test
    void shouldDeleteUserById() throws ServiceException, SQLException, DaoException {
        User user = DataGenerator.generateUser();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(accountDao.findAllByUserId(Mockito.any(), Mockito.anyLong())).thenReturn(List.of());
        Mockito.when(userDao.deleteById(Mockito.any(), Mockito.anyLong())).thenReturn(true);

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Boolean actual = service.deleteById(1L);
            Assertions.assertThat(actual).isTrue();

        }

    }

    @Test
    void shouldDeleteUserByIdNotExist() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.empty());

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.deleteById(1L))
                    .isInstanceOf(ServiceException.class);

        }

    }

    @Test
    void shouldDeleteUserByIdButHasAccounts() throws ServiceException, SQLException, DaoException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(DataGenerator.generateUser()));
        Mockito.when(accountDao.findAllByUserId(Mockito.any(), Mockito.anyLong()))
                .thenReturn(List.of(DataGenerator.generateAccount()));

        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException().isThrownBy(() -> service.deleteById(1L))
                    .isInstanceOf(ServiceException.class);

        }

    }

    @Test
    void shouldCreateUser() throws ServiceException, SQLException, DaoException {
        User user = DataGenerator.generateUser();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.create(Mockito.any(), Mockito.any()))
                .thenReturn(user);
        UserDto expected = modelMapper.map(user, UserDto.class);
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            UserDto actual = service.create(DataGenerator.generateUserDto());

            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }


    @Test
    void shouldUpdateUser() throws ServiceException, SQLException, DaoException {
        User user = DataGenerator.generateUser();
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(userDao.update(Mockito.any(), Mockito.any()))
                .thenReturn(user);

        UserDto expected = modelMapper.map(user, UserDto.class);
        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            UserDto actual = service.update(DataGenerator.generateUserDto());

            Assertions.assertThat(actual).isEqualTo(expected);

        }

    }

    @Test
    void shouldUpdateWithIdNotFoundThrowServiceException() throws ServiceException, SQLException, DaoException {

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(userDao.findById(Mockito.any(), Mockito.anyLong()))
                .thenReturn(Optional.empty());


        try (MockedStatic<ConnectionPool> pool = Mockito.mockStatic(ConnectionPool.class)) {
            pool.when(ConnectionPool::getInstance).thenReturn(dataSource);
            Assertions.assertThatException()
                    .isThrownBy(() -> service.update(DataGenerator.generateUserDto()))
                    .isExactlyInstanceOf(ServiceException.class)
                    .asInstanceOf(InstanceOfAssertFactories.throwable(ServiceException.class))
                    .extracting(ServiceException::getHttpCode)
                    .isEqualTo(CustomError.NOT_FOUND);


        }

    }




}
