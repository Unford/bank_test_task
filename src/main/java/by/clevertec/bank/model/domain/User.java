package by.clevertec.bank.model.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
