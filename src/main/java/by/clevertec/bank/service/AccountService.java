package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;

public interface AccountService extends CrudService<AccountDto> {
    void accrueIncome(int percent) throws ServiceException;
}
