package by.clevertec.bank.service.impl;

import by.clevertec.bank.dao.EntityTransaction;
import by.clevertec.bank.dao.impl.AccountDaoImpl;
import by.clevertec.bank.dao.impl.AccountTransactionDaoIml;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.*;
import by.clevertec.bank.service.AccountService;
import by.clevertec.bank.util.DataMapper;
import by.clevertec.bank.util.PdfFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
                if (s.signum() == 1) {
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
    public BigDecimal getAccountBalance(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(accountDao);
            accountDao.findById(id).orElseThrow(() ->
                    new ServiceException("Account is not found", CustomError.NOT_FOUND));
            return accountDao.sumAllByAccountId(id);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountExtractDto getAccountExtract(AccountExtractDto extractDto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            AccountTransactionDaoIml transactionDaoIml = new AccountTransactionDaoIml();
            transaction.initialize(accountDao, transactionDaoIml);
            Long id = extractDto.getAccount().getId();
            Account account = accountDao.findById(id)
                    .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND));
            AccountDto accountDto = DataMapper.getModelMapper().map(account, AccountDto.class);
            extractDto.setAccount(accountDto);
            extractDto.setBalance(accountDao.sumAllByAccountId(id));
            extractDto.setTransactions(transactionDaoIml.findAllByIdAndBetweenDates(id,
                            extractDto.getFrom(), extractDto.getTo())
                    .stream().map(v -> DataMapper.getModelMapper().map(v, TransactionDto.class)).toList());
            PdfFileUtils.saveAccountExtract(extractDto);
            return extractDto;
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountStatementDto getAccountStatement(AccountStatementDto statementDto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            AccountTransactionDaoIml transactionDaoIml = new AccountTransactionDaoIml();
            transaction.initialize(accountDao, transactionDaoIml);
            Long id = statementDto.getAccount().getId();
            Account account = accountDao.findById(id)
                    .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND));
            AccountDto accountDto = DataMapper.getModelMapper().map(account, AccountDto.class);
            statementDto.setAccount(accountDto);
            statementDto.setMoney(accountDao.calculateMoneyDataAllByIdAndBetweenDates(id,
                    statementDto.getFrom(), statementDto.getTo()));
            PdfFileUtils.saveAccountStatement(statementDto);
            return statementDto;
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public List<AccountDto> findAll() throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(accountDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            return accountDao.findAll().stream()
                    .map(e -> modelMapper.map(e, AccountDto.class)).toList();
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountDto findById(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(accountDao);
            ModelMapper modelMapper = DataMapper.getModelMapper();
            return modelMapper.map(accountDao.findById(id)
                    .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND)),
                    AccountDto.class);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean deleteById(long id) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            AccountTransactionDaoIml transactionDaoIml = new AccountTransactionDaoIml();
            transaction.initialize(accountDao, transactionDaoIml);
            Account account = accountDao.findById(id)
                    .orElseThrow(() -> new ServiceException("Account is not found", CustomError.NOT_FOUND));
            if (!transactionDaoIml.findAllByAccount(account.getAccount()).isEmpty()) {
                throw new ServiceException("Conflict account has transactions", CustomError.CONFLICT);
            }
            return accountDao.deleteById(id);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountDto create(AccountDto dto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(accountDao);
            if (accountDao.findByAccountOrBankAndUser(dto.getAccount(),
                    dto.getBank().getId(), dto.getUser().getId()).isPresent()) {
                throw new ServiceException("Account already exist!",  CustomError.CONFLICT);
            } else {
                Account account = accountDao.create(DataMapper.getModelMapper().map(dto, Account.class));
                return DataMapper.getModelMapper().map(account, AccountDto.class);
            }

        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }

    @Override
    public AccountDto update(AccountDto dto) throws ServiceException {
        EntityTransaction transaction = new EntityTransaction();
        try (transaction) {
            AccountDaoImpl accountDao = new AccountDaoImpl();
            transaction.initialize(accountDao);
            Optional<Account> optionalAccount = accountDao
                    .findByAccountOrBankAndUser(dto.getAccount(), 0, 0);
            if (optionalAccount.isPresent() && !optionalAccount.get().getId().equals(dto.getId())) {
                throw new ServiceException("Account already exist!", CustomError.CONFLICT);
            } else {
                Account account = accountDao.update(DataMapper.getModelMapper().map(dto, Account.class));
                return DataMapper.getModelMapper().map(account, AccountDto.class);
            }

        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException(e);
        }
    }
}
