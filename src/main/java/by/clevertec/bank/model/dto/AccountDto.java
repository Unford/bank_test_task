package by.clevertec.bank.model.dto;

import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    @Positive
    @NotNull
    private Long id;
    private BigDecimal balance;
    private String account;
    private LocalDate openDate;
    private LocalDate lastAccrualDate;
    private BankDto bank;
    private UserDto owner;

}
