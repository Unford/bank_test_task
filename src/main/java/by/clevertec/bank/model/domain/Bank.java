package by.clevertec.bank.model.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bank extends AbstractDaoEntity{
    private String name;
}
