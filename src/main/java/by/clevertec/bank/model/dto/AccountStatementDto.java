package by.clevertec.bank.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementDto {
    @NotNull
    @Valid
    private AccountDto account;
    private MoneyStatsDto money;
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;
}
