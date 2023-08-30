package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.dto.TransactionDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountTransactionService extends CrudService<TransactionDto> {
    TransactionDto deposit(TransactionDto transactionDto) throws ServiceException;

    TransactionDto withdrawal(BigDecimal sum, Account account) throws ServiceException;

    TransactionDto transferMoney(BigDecimal sum, Account from, Account to) throws ServiceException;

    TransactionDto accrueIncome(double percent) throws ServiceException;


    List<TransactionDto> findAllByAccount(String account) throws ServiceException;
}
