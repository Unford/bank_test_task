package by.clevertec.bank.model.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Account extends AbstractDaoEntity {
    private String account;
    private LocalDate openDate;
    private LocalDate lastAccrualDate;
    private Bank bank;
    private User owner;
    @Builder
    public Account(Long id, String account, LocalDate openDate, LocalDate lastAccrualDate,
                   Bank bank, User owner) {
        super(id);
        this.account = account;
        this.openDate = openDate;
        this.lastAccrualDate = lastAccrualDate;
        this.bank = bank;
        this.owner = owner;
    }
}
