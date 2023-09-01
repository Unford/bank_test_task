package by.clevertec.bank.service;

import by.clevertec.bank.exception.ServiceException;

import java.util.List;


/**
 * The `CrudService` interface is a generic interface that defines common CRUD (Create, Read, Update, Delete) operations
 * for a specific type `T`. It provides methods for finding all objects of type `T`, finding an object by its unique
 * identifier, deleting an object by its ID, creating a new object, and updating an existing object.
 */
public interface CrudService<T> {
    /**
     * The function `findAll()` returns a list of objects of type `T` and throws a `ServiceException` if an error occurs.
     *
     * @return The method `findAll()` returns a list of objects of type `T`.
     */
    List<T> findAll() throws ServiceException;

    /**
     * The findById function retrieves an object by its unique identifier.
     *
     * @param id The id parameter is a long value that represents the unique identifier of the object you want to find.
     * @return The method findById is returning an object of type T.
     */
    T findById(long id) throws ServiceException;

    /**
     * The function deletes a record from a database based on its ID and throws a ServiceException if an error occurs.
     *
     * @param id The id parameter is a long value that represents the unique identifier of the object that needs to be
     *           deleted.
     * @return The method is returning a boolean value.
     */
    boolean deleteById(long id) throws ServiceException;

    /**
     * The function creates a new object of type T and throws a ServiceException if an error occurs.
     *
     * @param dto The "dto" parameter is an object of type T, which represents a data transfer object. It is used as input
     *            to create a new entity or record in the system. The method returns the created entity or record.
     * @return The method is returning an object of type T.
     */
    T create(T dto) throws ServiceException;

    /**
     * The update function updates a DTO object and throws a ServiceException if an error occurs.
     *
     * @param dto The "dto" parameter is of type T, which represents a generic data transfer object. It is used to pass
     *            data between different layers or components of an application. In this case, the "update" method takes a dto object
     *            as input and returns an updated version of it. The method may also
     * @return The method is returning an object of type T.
     */
    T update(T dto) throws ServiceException;
}
