package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.AccountExtractDto;

import java.math.BigDecimal;

public interface AccountService extends CrudService<AccountDto> {
    void accrueIncome(int percent) throws ServiceException;

    BigDecimal getAccountBalance(long id) throws ServiceException;

    AccountExtractDto getAccountExtract(AccountExtractDto statementDto) throws ServiceException;
}
