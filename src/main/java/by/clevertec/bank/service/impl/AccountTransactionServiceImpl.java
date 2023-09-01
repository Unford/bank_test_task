package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.EntityTransaction;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AccountTransactionService;
import by.clevertec.bank.util.DataMapper;
import by.clevertec.bank.util.PdfFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(accountTransactionDao, accountDao);
            transactionDto.setFrom(null);
            if (accountDao.findById(transactionDto.getTo().getId()).isPresent()) {
                AccountTransaction createdDeposit = accountTransactionDao
                        .create(DataMapper.getModelMapper().map(transactionDto, AccountTransaction.class));
                PdfFileUtils.saveCheck(createdDeposit);
                return DataMapper.getModelMapper().map(createdDeposit, TransactionDto.class);
            } else {
                throw new ServiceException("Account not found!");
            }

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
            if (accountDao.findById(transactionDto.getTo().getId()).isPresent()) {
                BigDecimal sum = accountDao.sumAllByAccountId(transactionDto.getTo().getId());
                if (sum.add(transactionDto.getSum()).signum() >= 0) {
                    AccountTransaction createdWithdrawal = accountTransactionDao
                            .create(DataMapper.getModelMapper().map(transactionDto, AccountTransaction.class));
                    PdfFileUtils.saveCheck(createdWithdrawal);
                    return DataMapper.getModelMapper().map(createdWithdrawal, TransactionDto.class);
                } else {
                    throw new ServiceException("Not enough money to withdrawal");
                }
            } else {
                throw new ServiceException("Account not Found = " + transactionDto.getTo().getId());
            }


        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public TransactionDto transferMoney(TransactionDto transactionDto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountTransactionDaoIml accountTransactionDao = new AccountTransactionDaoIml();
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(accountTransactionDao, accountDao);
            if (accountDao.findById(transactionDto.getFrom().getId()).isPresent()) {
                if (accountDao.findById(transactionDto.getTo().getId()).isPresent()) {
                    BigDecimal money = accountDao.sumAllByAccountId(transactionDto.getFrom().getId());
                    if (money.subtract(transactionDto.getSum()).signum() >= 0) {
                        AccountTransaction createdTransfer = accountTransactionDao
                                .create(DataMapper.getModelMapper().map(transactionDto, AccountTransaction.class));
                        PdfFileUtils.saveCheck(createdTransfer);
                        return DataMapper.getModelMapper().map(createdTransfer, TransactionDto.class);
                    } else {
                        throw new ServiceException("Not enough money to transfer!");
                    }
                } else {
                    throw new ServiceException("To account not Found = " + transactionDto.getTo().getId());
                }
            } else {
                throw new ServiceException("From account not Found = " + transactionDto.getFrom().getId());
            }


        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
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
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public TransactionDto findById(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountTransactionDaoIml accountTransactionDao = new AccountTransactionDaoIml();
            transaction.initialize(accountTransactionDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            Optional<AccountTransaction> transactionOptional = accountTransactionDao.findById(id);
            return modelMapper.map(transactionOptional
                    .orElseThrow(() -> new ServiceException("Transaction is not found")), TransactionDto.class);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean deleteById(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountTransactionDaoIml transactionDaoIml = new AccountTransactionDaoIml();
            transaction.initialize(transactionDaoIml);
            transactionDaoIml.findById(id).orElseThrow(() -> new ServiceException("Transaction is not found"));
            return transactionDaoIml.deleteById(id);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public TransactionDto create(TransactionDto dto) throws ServiceException {
        throw new UnsupportedOperationException("Create operation unsupported for transaction service use specific methods");
    }

    @Override
    public TransactionDto update(TransactionDto dto) throws ServiceException {
        throw new UnsupportedOperationException("Update operation unsupported for transaction service");

    }
}
