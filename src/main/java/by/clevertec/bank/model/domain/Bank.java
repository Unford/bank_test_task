package by.clevertec.bank.model.domain;

import lombok.*;

/**
 * The Bank class is a subclass of AbstractDaoEntity and represents a bank entity with a name attribute.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Bank extends AbstractDaoEntity{
    private String name;
    @Builder
    public Bank(Long id, String name) {
        super(id);
        this.name = name;
    }
}
