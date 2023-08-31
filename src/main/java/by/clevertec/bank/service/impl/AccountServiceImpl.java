package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.EntityTransaction;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.util.DataMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger();
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100L);


    private AccountServiceImpl() {
    }

    private static final AccountServiceImpl instance = new AccountServiceImpl();

    public static AccountServiceImpl getInstance() {
        return instance;
    }


    @Override
    public void accrueIncome(int percent) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            AccountTransactionDaoIml transactionDao = new AccountTransactionDaoIml();
            transaction.initializeTransaction(accountDao, transactionDao);
            List<Account> accounts = accountDao.findAllAccrual();
            for (Account a : accounts) {
                logger.debug("accrue income -  {}", a);
                BigDecimal s = accountDao.sumAllByAccountId(a.getId());
                if (s.signum() == 1){
                    transactionDao.create(AccountTransaction.builder()
                            .sum(s.multiply(BigDecimal.valueOf(percent)).divide(ONE_HUNDRED))
                            .to(a).build());
                }
                accountDao.updateLastAccrualDate(a);

            }
            transaction.commit();
        } catch (DaoException e) {
            transaction.rollback();
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public BigDecimal getAccountSum(Long id) throws ServiceException {
        return null;
    }

    @Override
    public List<AccountDto> findAll() throws ServiceException {
        return null;
    }
}
