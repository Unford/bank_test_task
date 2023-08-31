package by.clevertec.bank.model.domain;

import lombok.*;

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
