package by.clevertec.bank.model.domain;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The `AccountTransaction` class represents a transaction between two accounts, including the sum, date and time, and the
 * accounts involved.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccountTransaction extends AbstractDaoEntity {
    private BigDecimal sum;
    private LocalDateTime dateTime;
    private Account to;
    private Account from;
    @Builder
    public AccountTransaction(Long id, BigDecimal sum, LocalDateTime dateTime, Account to, Account from) {
        super(id);
        this.sum = sum;
        this.dateTime = dateTime;
        this.to = to;
        this.from = from;
    }
}
