package by.clevertec.bank.service.impl;

import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.service.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LogManager.getLogger();

    private AccountServiceImpl() {
    }

    private static final AccountServiceImpl instance = new AccountServiceImpl();

    public static AccountServiceImpl getInstance() {
        return instance;
    }

    @Override
    public void accrueIncome(int percent) throws ServiceException {
        //todo
    }

    @Override
    public List<AccountDto> findAll() throws ServiceException {
        return null;
    }
}
