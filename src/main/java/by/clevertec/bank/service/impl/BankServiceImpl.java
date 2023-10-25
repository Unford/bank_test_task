package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.BankDaoImpl;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.dto.BankDto;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.AbstractService;
import by.clevertec.bank.service.CrudService;
import by.clevertec.bank.util.DataMapper;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

public class BankServiceImpl extends AbstractService<BankDto> implements CrudService<BankDto> {
    private final AccountDaoImpl accountDao;
    private final BankDaoImpl bankDao;
    private final ModelMapper modelMapper;

    public BankServiceImpl(AccountDaoImpl accountDao, BankDaoImpl bankDao, ModelMapper modelMapper) {
        this.accountDao = accountDao;
        this.bankDao = bankDao;
        this.modelMapper = modelMapper;
    }

    public BankServiceImpl() {
        this(new AccountDaoImpl(), new BankDaoImpl(), DataMapper.getModelMapper());
    }

    @Override
    public List<BankDto> findAll() throws ServiceException {
        return executeInTransactionalContext(connection -> bankDao.findAll(connection)
                .stream()
                .map(e -> modelMapper.map(e, BankDto.class)).toList());
    }

    @Override
    public BankDto findById(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            return modelMapper.map(bankDao.findById(connection, id)
                            .orElseThrow(() -> new ServiceException("Bank is not found", CustomError.NOT_FOUND)),
                    BankDto.class);
        });
    }

    @Override
    public boolean deleteById(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            bankDao.findById(connection, id)
                    .orElseThrow(() -> new ServiceException("Bank is not found", CustomError.NOT_FOUND));
            if (!accountDao.findAllByBankId(connection, id).isEmpty()) {
                throw new ServiceException("Conflict bank has accounts");
            }
            return bankDao.deleteById(connection, id);
        });
    }

    @Override
    public BankDto create(BankDto dto) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            if (bankDao.findByName(connection, dto.getName()).isPresent()) {
                throw new ServiceException("Bank is already exist!", CustomError.CONFLICT);
            } else {
                Bank bank = bankDao.create(connection, modelMapper.map(dto, Bank.class));
                return modelMapper.map(bank, BankDto.class);
            }
        });
    }

    @Override
    public BankDto update(BankDto dto) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            bankDao.findById(connection, dto.getId())
                    .orElseThrow(() -> new ServiceException("Bank is not found", CustomError.NOT_FOUND));
            Optional<Bank> optional = bankDao.findByName(connection, dto.getName());
            if (optional.isPresent() && !optional.get().getId().equals(dto.getId())) {
                throw new ServiceException("Bank is already exist!", CustomError.CONFLICT);
            } else {
                Bank bank = bankDao.update(connection, modelMapper.map(dto, Bank.class));
                return modelMapper.map(bank, BankDto.class);
            }
        });
    }
}
