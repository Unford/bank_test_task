package by.clevertec.bank.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class "AbstractDaoEntity" is a data class with a single field "id" of type Long, and it includes constructors for
 * both with and without arguments.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractDaoEntity {
    protected Long id;
}
