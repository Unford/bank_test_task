package by.clevertec.bank.model.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class Account extends AbstractDaoEntity {
    private String account;
    private LocalDate openDate;
    private LocalDate lastAccrualDate;
    private String currency;
    private Bank bank;
    private User owner;
}
