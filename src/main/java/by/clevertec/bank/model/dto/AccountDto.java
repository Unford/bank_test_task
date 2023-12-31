package by.clevertec.bank.model.dto;

import by.clevertec.bank.model.validation.CreateAccountValidationGroup;
import by.clevertec.bank.model.validation.UpdateValidationGroup;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * The AccountDto class is a data transfer object that represents an account, including its ID, account name, open date,
 * last accrual date, associated bank, and associated user.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    @Positive
    @NotNull
    private Long id;
    @NotBlank(groups = {CreateAccountValidationGroup.class, UpdateValidationGroup.class})
    private String account;
    private LocalDate openDate;
    private LocalDate lastAccrualDate;
    @NotNull(groups = CreateAccountValidationGroup.class)
    @Valid
    private BankDto bank;
    @NotNull(groups = CreateAccountValidationGroup.class)
    @Valid
    private UserDto user;


}
