package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.dao.impl.BankDaoImpl;
import by.clevertec.bank.dao.impl.UserDaoImpl;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.*;
import by.clevertec.bank.service.AbstractService;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.util.DataMapper;
import by.clevertec.bank.util.PdfFileUtils;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The `AccountServiceImpl` class is a Java implementation of the `AccountService` interface that provides various methods
 * for managing and manipulating account data.
 */
public class AccountServiceImpl extends AbstractService<AccountDto> implements AccountService {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100L);
    private final AccountTransactionDaoIml transactionDao;
    private final AccountDaoImpl accountDao;
    private final BankDaoImpl bankDao;
    private final UserDaoImpl userDao;
    private final ModelMapper modelMapper;

    public AccountServiceImpl(AccountTransactionDaoIml transactionDao,
                              AccountDaoImpl accountDao,
                              BankDaoImpl bankDao,
                              UserDaoImpl userDao,
                              ModelMapper modelMapper) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.bankDao = bankDao;
        this.userDao = userDao;
        this.modelMapper = modelMapper;
    }

    public AccountServiceImpl() {
        this.transactionDao = new AccountTransactionDaoIml();
        this.accountDao = new AccountDaoImpl();
        this.bankDao = new BankDaoImpl();
        this.userDao = new UserDaoImpl();
        this.modelMapper = DataMapper.getModelMapper();
    }

    @Override
    public void accrueIncome(int percent) throws ServiceException {
        executeInTransactionalContext(true, (connection -> {
            List<Account> accounts = accountDao.findAllAccrual(connection);
            for (Account a : accounts) {
                logger.debug("accrue income -  {}", a);
                BigDecimal s = accountDao.sumAllByAccountId(connection, a.getId());
                if (s.signum() == 1) {
                    transactionDao.create(connection, AccountTransaction.builder()
                            .sum(s.multiply(BigDecimal.valueOf(percent))
                                    .divide(ONE_HUNDRED))
                            .to(a)
                            .build());
                }
                accountDao.updateLastAccrualDate(connection, a);
            }
        }));
    }

    @Override
    public BigDecimal getAccountBalance(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            accountDao.findById(connection, id).orElseThrow(() ->
                    new ServiceException("Account is not found", CustomError.NOT_FOUND));
            return accountDao.sumAllByAccountId(connection, id);
        });
    }

    @Override
    public AccountExtractDto getAccountExtract(AccountExtractDto extractDto) throws ServiceException {
        AccountExtractDto accountExtractDto = executeInTransactionalContext(connection -> {
            Long id = extractDto.getAccount().getId();
            Account account = accountDao.findById(connection, id)
                    .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND));
            AccountDto accountDto = modelMapper.map(account, AccountDto.class);
            extractDto.setAccount(accountDto);
            extractDto.setBalance(accountDao.sumAllByAccountId(connection, id));
            extractDto.setTransactions(transactionDao.findAllByIdAndBetweenDates(connection, id,
                            extractDto.getFrom(), extractDto.getTo())
                    .stream().map(v -> modelMapper.map(v, TransactionDto.class)).toList());
            return extractDto;
        });
        PdfFileUtils.saveAccountExtract(accountExtractDto);
        return accountExtractDto;
    }

    @Override
    public AccountStatementDto getAccountStatement(AccountStatementDto statementDto) throws ServiceException {
        AccountStatementDto performed = executeInTransactionalContext(connection -> {
            Long id = statementDto.getAccount().getId();
            Account account = accountDao.findById(connection, id)
                    .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND));
            AccountDto accountDto = modelMapper.map(account, AccountDto.class);
            statementDto.setAccount(accountDto);
            statementDto.setMoney(accountDao.calculateMoneyDataAllByIdAndBetweenDates(connection, id,
                    statementDto.getFrom(), statementDto.getTo()));
            return statementDto;
        });
        PdfFileUtils.saveAccountStatement(performed);
        return performed;
    }

    @Override
    public List<AccountDto> findAll() throws ServiceException {
        return executeInTransactionalContext(connection -> accountDao.findAll(connection).stream()
                .map(e -> modelMapper.map(e, AccountDto.class)).toList());
    }

    @Override
    public AccountDto findById(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            return modelMapper.map(accountDao.findById(connection, id)
                            .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND)),
                    AccountDto.class);
        });

    }

    @Override
    public boolean deleteById(long id) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            Account account = accountDao.findById(connection, id)
                    .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND));
            if (!transactionDao.findAllByAccount(connection, account.getAccount()).isEmpty()) {
                throw new ServiceException("Conflict account has transactions", CustomError.CONFLICT);
            }
            return accountDao.deleteById(connection, id);
        });
    }

    @Override
    public AccountDto create(AccountDto dto) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            bankDao.findById(connection, dto.getBank().getId())
                    .orElseThrow(() -> new ServiceException("Bank is not found", CustomError.NOT_FOUND));
            userDao.findById(connection, dto.getUser().getId())
                    .orElseThrow(() -> new ServiceException("User is not found", CustomError.NOT_FOUND));
            if (accountDao.findByAccountOrBankAndUser(connection, dto.getAccount(),
                    dto.getBank().getId(), dto.getUser().getId()).isPresent()) {
                throw new ServiceException("Account already exist!", CustomError.CONFLICT);
            } else {
                Account account = accountDao.create(connection, modelMapper.map(dto, Account.class));
                return modelMapper.map(account, AccountDto.class);
            }
        });

    }

    @Override
    public AccountDto update(AccountDto dto) throws ServiceException {
        return executeInTransactionalContext(connection -> {
            accountDao.findById(connection, dto.getId())
                    .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND));
            Optional<Account> optionalAccount = accountDao
                    .findByAccountOrBankAndUser(connection, dto.getAccount(), 0, 0);
            if (optionalAccount.isPresent() && !optionalAccount.get().getId().equals(dto.getId())) {
                throw new ServiceException("Account already exist!", CustomError.CONFLICT);
            } else {
                Account account = accountDao.update(connection, modelMapper.map(dto, Account.class));
                return modelMapper.map(account, AccountDto.class);
            }
        });

    }
}
