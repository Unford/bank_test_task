package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.UserDaoImpl;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.User;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.model.dto.UserDto;
import by.clevertec.bank.service.AbstractService;
import by.clevertec.bank.service.CrudService;
import by.clevertec.bank.util.DataMapper;
import org.modelmapper.ModelMapper;

import java.util.List;

public class UserServiceImpl extends AbstractService<UserDto> implements CrudService<UserDto> {
    private final AccountDaoImpl accountDao;
    private final UserDaoImpl userDao;
    private final ModelMapper modelMapper;

    public UserServiceImpl(AccountDaoImpl accountDao, UserDaoImpl userDao, ModelMapper modelMapper) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.modelMapper = modelMapper;
    }

    public UserServiceImpl() {
        this(new AccountDaoImpl(), new UserDaoImpl(), DataMapper.getModelMapper());
    }

    @Override
    public List<UserDto> findAll() throws ServiceException {
        return executeInTransactionalContext(connection -> {
            return userDao.findAll(connection)
                    .stream()
                    .map(e -> modelMapper.map(e, UserDto.class)).toList();
        });
    }

    @Override
    public UserDto findById(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            return modelMapper.map(userDao.findById(connection, id)
                            .orElseThrow(() -> new ServiceException("User is not found", CustomError.NOT_FOUND)),
                    UserDto.class);
        });
    }

    @Override
    public boolean deleteById(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            userDao.findById(connection, id)
                    .orElseThrow(() -> new ServiceException("User is not found", CustomError.NOT_FOUND));
            if (!accountDao.findAllByUserId(connection, id).isEmpty()) {
                throw new ServiceException("Conflict user has accounts");
            }
            return userDao.deleteById(connection, id);
        });
    }

    @Override
    public UserDto create(UserDto dto) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            User user = userDao.create(connection, modelMapper.map(dto, User.class));
            return modelMapper.map(user, UserDto.class);
        });
    }

    @Override
    public UserDto update(UserDto dto) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            userDao.findById(connection, dto.getId())
                    .orElseThrow(() -> new ServiceException("User is not found", CustomError.NOT_FOUND));
            User user = userDao.update(connection, modelMapper.map(dto, User.class));
            return modelMapper.map(user, UserDto.class);
        });
    }
}
