package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;

import java.util.List;

public interface CrudService<T> {
    List<T> findAll() throws ServiceException;
}
