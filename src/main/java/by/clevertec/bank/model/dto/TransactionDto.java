package by.clevertec.bank.model.dto;

import by.clevertec.bank.model.validation.TransferValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Long id;
    @NotNull
    @Positive
    private BigDecimal sum;
    @Valid
    @NotNull(groups = TransferValidationGroup.class)
    private AccountDto from;
    @NotNull
    @Valid
    private AccountDto to;
    private LocalDateTime dateTime;
}
