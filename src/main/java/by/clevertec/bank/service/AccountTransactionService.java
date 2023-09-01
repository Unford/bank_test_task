package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.TransactionDto;

import java.util.List;

/**
 * The `AccountTransactionService` interface extends the `CrudService<TransactionDto>` interface, which means it inherits
 * all the methods defined in the `CrudService` interface with the generic type `TransactionDto`.
 */
public interface AccountTransactionService extends CrudService<TransactionDto> {
    /**
     * The deposit function takes a TransactionDto object as input and returns a TransactionDto object, while also
     * potentially throwing a ServiceException.
     *
     * @param transactionDto The transactionDto parameter is an object of type TransactionDto, which represents a
     *                       transaction. It likely contains information such as the amount to be deposited and the account to deposit into.
     * @return The method is returning a TransactionDto object.
     */
    TransactionDto deposit(TransactionDto transactionDto) throws ServiceException;

    /**
     * The function performs a withdrawal transaction and returns a TransactionDto object.
     *
     * @param transactionDto The transactionDto parameter is an object of type TransactionDto. It represents the details of
     *                       a transaction, such as the amount to be withdrawn and any additional information related to the transaction.
     * @return The method is returning a TransactionDto object.
     */
    TransactionDto withdrawal(TransactionDto transactionDto) throws ServiceException;

    /**
     * The function transfers money based on the provided transaction details and returns the updated transaction
     * information.
     *
     * @param transactionDto The transactionDto parameter is an object of type TransactionDto, which represents the details
     *                       of a money transfer transaction. It likely contains information such as the sender's account number, the recipient's
     *                       account number, and the amount to be transferred.
     * @return The method is returning a TransactionDto object.
     */
    TransactionDto transferMoney(TransactionDto transactionDto) throws ServiceException;


    /**
     * The function returns a list of TransactionDto objects that are associated with a specific account.
     *
     * @param account The account parameter is a string that represents the account for which we want to find all
     *                transactions.
     * @return The method `findAllByAccount` returns a list of `TransactionDto` objects.
     */
    List<TransactionDto> findAllByAccount(String account) throws ServiceException;
}
