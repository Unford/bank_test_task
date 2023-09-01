package by.clevertec.bank.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * The class AccountStatementDto is a data transfer object that represents an account statement with information about the
 * account, money statistics, and the date range.
 */
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
