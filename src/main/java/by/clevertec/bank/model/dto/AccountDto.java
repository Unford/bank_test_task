package by.clevertec.bank.model.dto;

import by.clevertec.bank.model.validation.CreateValidationGroup;
import by.clevertec.bank.model.validation.UpdateValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private String account;
    private LocalDate openDate;
    private LocalDate lastAccrualDate;
    @NotNull(groups = CreateValidationGroup.class)
    @Valid
    private BankDto bank;
    @NotNull(groups = CreateValidationGroup.class)
    @Valid
    private UserDto user;


}
