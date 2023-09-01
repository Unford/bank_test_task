package by.clevertec.bank.model.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The User class is a subclass of AbstractDaoEntity and represents a user with a full name.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends AbstractDaoEntity {
    private String fullName;

    @Builder
    public User(Long id, String fullName) {
        super(id);
        this.fullName = fullName;
    }
}
