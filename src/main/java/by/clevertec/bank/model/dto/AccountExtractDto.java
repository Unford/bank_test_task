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
import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class AccountExtractDto {
    @NotNull
    @Valid
    private AccountDto account;
    private BigDecimal balance;
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;
    private List<TransactionDto> transactions;


}
