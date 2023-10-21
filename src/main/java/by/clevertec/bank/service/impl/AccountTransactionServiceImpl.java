package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.AbstractService;
import by.clevertec.bank.service.AccountTransactionService;
import by.clevertec.bank.util.DataMapper;
import by.clevertec.bank.util.PdfFileUtils;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The `AccountTransactionServiceImpl` class is a Java implementation of the `AccountTransactionService` interface that
 * provides methods for depositing, withdrawing, transferring money, and managing transactions.
 */
public class AccountTransactionServiceImpl extends AbstractService<TransactionDto> implements AccountTransactionService {
    private final AccountDaoImpl accountDao;
    private final AccountTransactionDaoIml transactionDao;
    private final ModelMapper modelMapper;

    public AccountTransactionServiceImpl(AccountDaoImpl accountDao,
                                         AccountTransactionDaoIml transactionDao,
                                         ModelMapper modelMapper) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.modelMapper = modelMapper;
    }

    public AccountTransactionServiceImpl() {
        this(new AccountDaoImpl(), new AccountTransactionDaoIml(), DataMapper.getModelMapper());
    }

    @Override
    public TransactionDto deposit(TransactionDto transactionDto) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            transactionDto.setFrom(null);
            if (accountDao.findById(connection, transactionDto.getTo().getId()).isPresent()) {
                AccountTransaction createdDeposit = transactionDao
                        .create(connection, modelMapper.map(transactionDto, AccountTransaction.class));
                PdfFileUtils.saveCheck(createdDeposit);
                return modelMapper.map(createdDeposit, TransactionDto.class);
            } else {
                throw new ServiceException("Account not found!", CustomError.NOT_FOUND);
            }
        });
    }

    @Override
    public TransactionDto withdrawal(TransactionDto transactionDto) throws ServiceException {
        transactionDto.setSum(transactionDto.getSum().negate());
        transactionDto.setFrom(null);
        AccountTransaction check = executeInTransactionalContext(connection -> {
            if (accountDao.findById(connection, transactionDto.getTo().getId()).isPresent()) {
                BigDecimal sum = accountDao.sumAllByAccountId(connection, transactionDto.getTo().getId());
                if (sum.add(transactionDto.getSum()).signum() >= 0) {
                    return transactionDao.create(connection, modelMapper.map(transactionDto, AccountTransaction.class));
                } else {
                    throw new ServiceException("Not enough money to withdrawal!", CustomError.CONFLICT);
                }
            } else {
                throw new ServiceException("Account not Found!", CustomError.NOT_FOUND);
            }
        });
        PdfFileUtils.saveCheck(check);
        return modelMapper.map(check, TransactionDto.class);
    }

    @Override
    public TransactionDto transferMoney(TransactionDto transactionDto) throws ServiceException {
        return executeInTransactionalContext(true, connection -> {
            if (accountDao.findById(connection, transactionDto.getFrom().getId()).isPresent()) {
                if (accountDao.findById(connection, transactionDto.getTo().getId()).isPresent()) {
                    BigDecimal money = accountDao.sumAllByAccountId(connection, transactionDto.getFrom().getId());
                    if (money.subtract(transactionDto.getSum()).signum() >= 0) {
                        AccountTransaction createdTransfer = transactionDao
                                .create(connection, modelMapper.map(transactionDto, AccountTransaction.class));
                        PdfFileUtils.saveCheck(createdTransfer);
                        return modelMapper.map(createdTransfer, TransactionDto.class);
                    } else {
                        throw new ServiceException("Not enough money to transfer!", CustomError.CONFLICT);
                    }
                } else {
                    throw new ServiceException("To account not Found!", CustomError.NOT_FOUND);
                }
            } else {
                throw new ServiceException("From account not Found!", CustomError.NOT_FOUND);
            }
        });
    }


    @Override
    public List<TransactionDto> findAllByAccount(String account) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            return transactionDao.findAllByAccount(connection, account).stream()
                    .map(e -> modelMapper.map(e, TransactionDto.class)).toList();
        });
    }

    @Override
    public List<TransactionDto> findAllByAccountId(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            return transactionDao.findAllByAccountId(connection, id).stream()
                    .map(e -> modelMapper.map(e, TransactionDto.class)).toList();
        });

    }


    @Override
    public List<TransactionDto> findAll() throws ServiceException {
        return executeInTransactionalContext(connection -> {
            return transactionDao.findAll(connection).stream()
                    .map(e -> modelMapper.map(e, TransactionDto.class)).toList();
        });
    }

    @Override
    public TransactionDto findById(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            Optional<AccountTransaction> transactionOptional = transactionDao.findById(connection, id);
            return modelMapper.map(transactionOptional
                            .orElseThrow(() -> new ServiceException("Transaction is not found", CustomError.NOT_FOUND)),
                    TransactionDto.class);
        });
    }

    @Override
    public boolean deleteById(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            transactionDao.findById(connection, id)
                    .orElseThrow(() -> new ServiceException("Transaction is not found", CustomError.NOT_FOUND));
            return transactionDao.deleteById(connection, id);
        });
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
