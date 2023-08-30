package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.AccountTransactionDao;
import by.clevertec.bank.dao.ConnectionPool;
import by.clevertec.bank.dao.EntityTransaction;
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

import java.math.BigDecimal;

public class AccountTransactionServiceImpl implements AccountTransactionService {
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
            AccountTransaction createdDeposit = accountTransactionDao
                    .deposit(DataMapper.getModelMapper().map(transactionDto, AccountTransaction.class));
            return DataMapper.getModelMapper().map(createdDeposit, TransactionDto.class);
        } catch (DaoException e) {
            transaction.rollback();
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public TransactionDto withdrawal(BigDecimal sum, Account account) throws ServiceException {
        return null;
    }

    @Override
    public TransactionDto transferMoney(BigDecimal sum, Account from, Account to) throws ServiceException {
        return null;
    }

    @Override
    public TransactionDto accrueIncome(double percent) throws ServiceException {
        return null;
    }


}
