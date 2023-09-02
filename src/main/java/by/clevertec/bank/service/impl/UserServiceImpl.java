package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.EntityTransaction;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.UserDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.User;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.model.dto.UserDto;
import by.clevertec.bank.service.CrudService;
import by.clevertec.bank.util.DataMapper;
import org.modelmapper.ModelMapper;

import java.util.List;

public class UserServiceImpl implements CrudService<UserDto> {

    private UserServiceImpl() {
    }

    private static final UserServiceImpl instance = new UserServiceImpl();

    public static UserServiceImpl getInstance() {
        return instance;
    }

    @Override
    public List<UserDto> findAll() throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            UserDaoImpl userDao = new UserDaoImpl();
            transaction.initialize(userDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            return userDao.findAll().stream().map(e -> modelMapper.map(e, UserDto.class)).toList();
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public UserDto findById(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            UserDaoImpl userDao = new UserDaoImpl();
            transaction.initialize(userDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            return modelMapper.map(userDao.findById(id)
                            .orElseThrow(() -> new ServiceException("User is not found", CustomError.NOT_FOUND)),
                    UserDto.class);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean deleteById(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            UserDaoImpl userDao = new UserDaoImpl();
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(userDao, accountDao);
            userDao.findById(id)
                    .orElseThrow(() -> new ServiceException("User is not found", CustomError.NOT_FOUND));
            if (!accountDao.findAllByUserId(id).isEmpty()){
                throw new ServiceException("Conflict user has accounts");
            }
            return userDao.deleteById(id);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public UserDto create(UserDto dto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            UserDaoImpl userDao = new UserDaoImpl();
            transaction.initialize(userDao);
            User user = userDao.create(DataMapper.getModelMapper().map(dto, User.class));
            return DataMapper.getModelMapper().map(user, UserDto.class);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public UserDto update(UserDto dto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            UserDaoImpl userDao = new UserDaoImpl();
            transaction.initialize(userDao);
            userDao.findById(dto.getId())
                    .orElseThrow(() -> new ServiceException("User is not found", CustomError.NOT_FOUND));
            User user = userDao.update(DataMapper.getModelMapper().map(dto, User.class));
            return DataMapper.getModelMapper().map(user, UserDto.class);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }
}
