package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;

import java.util.List;

public interface CrudService<T> {
    List<T> findAll() throws ServiceException;

    T findById(long id) throws ServiceException;

    boolean deleteById(long id) throws ServiceException;

    T create(T dto) throws ServiceException;

    T update(T dto) throws ServiceException;
}
