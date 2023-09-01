package by.clevertec.bank.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class MoneyStatsDto {
    private BigDecimal balance;
    private BigDecimal income;
    private BigDecimal expenditure;
}
