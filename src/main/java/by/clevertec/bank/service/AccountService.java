package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.AccountExtractDto;
import by.clevertec.bank.model.dto.AccountStatementDto;

import java.math.BigDecimal;

/**
 * The `AccountService` interface defines a set of methods that can be used to perform operations related to bank accounts.
  */
public interface AccountService extends CrudService<AccountDto> {
    /**
     * The function accrueIncome calculates and adds income based on a given percentage.
     *
     * @param percent The percent parameter represents the percentage of income to be accrued.
     */
    void accrueIncome(int percent) throws ServiceException;

    /**
     * The function "getAccountBalance" returns the account balance for a given account ID.
     *
     * @param id The id parameter is a long value that represents the unique identifier of the account for which you want
     * to retrieve the balance.
     * @return The method is returning a BigDecimal value, which represents the account balance.
     */
    BigDecimal getAccountBalance(long id) throws ServiceException;

    /**
     * The function "getAccountExtract" retrieves an account extract using the provided AccountExtractDto object and throws
     * a ServiceException if an error occurs.
     *
     * @param extractDto An object of type AccountExtractDto that contains the necessary information for retrieving an
     * account extract.
     * @return The method is returning an object of type AccountExtractDto.
     */
    AccountExtractDto getAccountExtract(AccountExtractDto extractDto) throws ServiceException;

    /**
     * The function "getAccountStatement" retrieves an account statement and throws a ServiceException if an error occurs.
     *
     * @param statementDto The statementDto parameter is an object of type AccountStatementDto. It is used to pass the
     * necessary information required to generate an account statement.
     * @return The method is returning an object of type AccountStatementDto.
     */
    AccountStatementDto getAccountStatement(AccountStatementDto statementDto) throws ServiceException;

}
