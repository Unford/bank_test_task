package by.clevertec.bank.model.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountTransaction extends AbstractDaoEntity {
    private BigDecimal sum;
    private LocalDateTime dateTime;
    private Account recipientAccount;
    private Account sendersAccount;

}
