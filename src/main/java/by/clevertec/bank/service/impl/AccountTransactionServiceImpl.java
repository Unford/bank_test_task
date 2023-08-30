package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.EntityTransaction;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AccountTransactionService;
import by.clevertec.bank.util.DataMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;

public final class AccountTransactionServiceImpl implements AccountTransactionService {
    private static final Logger logger = LogManager.getLogger();

    private AccountTransactionServiceImpl() {
    }

    private static final AccountTransactionServiceImpl instance = new AccountTransactionServiceImpl();

    public static AccountTransactionServiceImpl getInstance() {
        return instance;
    }

    @Override
    public TransactionDto deposit(TransactionDto transactionDto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountTransactionDaoIml accountTransactionDao = new AccountTransactionDaoIml();
            transaction.initialize(accountTransactionDao);
            transactionDto.setFrom(null);
            AccountTransaction createdDeposit = accountTransactionDao
                    .create(DataMapper.getModelMapper().map(transactionDto, AccountTransaction.class));
            return DataMapper.getModelMapper().map(createdDeposit, TransactionDto.class);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public TransactionDto withdrawal(TransactionDto transactionDto) throws ServiceException {
        transactionDto.setSum(transactionDto.getSum().negate());
        transactionDto.setFrom(null);
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountTransactionDaoIml accountTransactionDao = new AccountTransactionDaoIml();
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(accountTransactionDao, accountDao);

            BigDecimal sum = accountDao.sumAllByAccountId(transactionDto.getTo().getId());
            if (sum.add(transactionDto.getSum()).signum() >= 0){
                AccountTransaction createdDeposit = accountTransactionDao
                        .create(DataMapper.getModelMapper().map(transactionDto, AccountTransaction.class));
                return DataMapper.getModelMapper().map(createdDeposit, TransactionDto.class);
            }else {
                throw new ServiceException("Not enough money to withdrawal");
            }
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public TransactionDto transferMoney(TransactionDto transactionDto) throws ServiceException {
        return null;
    }



    @Override
    public List<TransactionDto> findAllByAccount(String account) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountTransactionDaoIml accountTransactionDao = new AccountTransactionDaoIml();
            transaction.initialize(accountTransactionDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            return accountTransactionDao.findAllByAccount(account).stream()
                    .map(e -> modelMapper.map(e, TransactionDto.class)).toList();
        } catch (DaoException e) {
            transaction.rollback();
            logger.error(e);
            throw new ServiceException(e);
        }
    }


    @Override
    public List<TransactionDto> findAll() throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountTransactionDaoIml accountTransactionDao = new AccountTransactionDaoIml();
            transaction.initialize(accountTransactionDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            return accountTransactionDao.findAll().stream()
                    .map(e -> modelMapper.map(e, TransactionDto.class)).toList();
        } catch (DaoException e) {
            transaction.rollback();
            logger.error(e);
            throw new ServiceException(e);
        }
    }
}
