package by.clevertec.bank.model.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractDaoEntity {
    private String fullName;
}
