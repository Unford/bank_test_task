package by.clevertec.bank.dao;

import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.model.domain.Bank;

import java.util.Optional;

/**
 * The code is defining an interface called `BankDao`. This interface declares a method called `findByName` that takes a
 * `String` parameter `name` and returns an `Optional` object of type `Bank`. The method also throws a `DaoException`. This
 * interface is likely used for accessing and manipulating data related to banks in a banking application.
 */
public interface BankDao {
    Optional<Bank> findByName(String name) throws DaoException;
}
