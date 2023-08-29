package by.clevertec.bank.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@Jacksonized

public class TransactionDto {
    @NotNull
    @Positive
    private BigDecimal sum;
    @Valid
    private AccountDto from;
    @NotNull
    @Valid
    private AccountDto to;
}
