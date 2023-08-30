package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;

import java.util.List;

public interface AccountTransactionService extends CrudService<TransactionDto> {
    TransactionDto deposit(TransactionDto transactionDto) throws ServiceException;

    TransactionDto withdrawal(TransactionDto transactionDto) throws ServiceException;

    TransactionDto transferMoney(TransactionDto transactionDto) throws ServiceException;




    List<TransactionDto> findAllByAccount(String account) throws ServiceException;
}
