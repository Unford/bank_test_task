package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.EntityTransaction;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.BankDaoImpl;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.dto.BankDto;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.CrudService;
import by.clevertec.bank.util.DataMapper;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

public final class BankServiceImpl implements CrudService<BankDto> {

    private BankServiceImpl() {
    }

    private static final BankServiceImpl instance = new BankServiceImpl();

    public static BankServiceImpl getInstance() {
        return instance;
    }

    @Override
    public List<BankDto> findAll() throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            BankDaoImpl bankDao = new BankDaoImpl();
            transaction.initialize(bankDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            return bankDao.findAll().stream().map(e -> modelMapper.map(e, BankDto.class)).toList();
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public BankDto findById(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            BankDaoImpl bankDao = new BankDaoImpl();
            transaction.initialize(bankDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            return modelMapper.map(bankDao.findById(id)
                            .orElseThrow(() -> new ServiceException("Bank is not found", CustomError.NOT_FOUND)),
                    BankDto.class);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean deleteById(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            BankDaoImpl bankDao = new BankDaoImpl();
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(bankDao, accountDao);
            bankDao.findById(id)
                    .orElseThrow(() -> new ServiceException("Bank is not found", CustomError.NOT_FOUND));
            if (!accountDao.findAllByBankId(id).isEmpty()){
                throw new ServiceException("Conflict bank has accounts");
            }
            return bankDao.deleteById(id);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public BankDto create(BankDto dto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            BankDaoImpl bankDao = new BankDaoImpl();
            transaction.initialize(bankDao);
            if (bankDao.findByName(dto.getName()).isPresent()) {
                throw new ServiceException("Bank is already exist!", CustomError.CONFLICT);
            } else {
                Bank bank = bankDao.create(DataMapper.getModelMapper().map(dto, Bank.class));
                return DataMapper.getModelMapper().map(bank, BankDto.class);
            }
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public BankDto update(BankDto dto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            BankDaoImpl bankDao = new BankDaoImpl();
            transaction.initialize(bankDao);
            bankDao.findById(dto.getId())
                    .orElseThrow(() -> new ServiceException("Bank is not found", CustomError.NOT_FOUND));
            Optional<Bank> optional = bankDao.findByName(dto.getName());
            if (optional.isPresent() && !optional.get().getId().equals(dto.getId())) {
                throw new ServiceException("Bank is already exist!", CustomError.CONFLICT);
            } else {
                Bank bank = bankDao.update(DataMapper.getModelMapper().map(dto, Bank.class));
                return DataMapper.getModelMapper().map(bank, BankDto.class);
            }
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }
}
