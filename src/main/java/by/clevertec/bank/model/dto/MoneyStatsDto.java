package by.clevertec.bank.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

/**
 * The class MoneyStatsDto is a data transfer object that represents financial statistics including balance, income, and
 * expenditure, and it is annotated with Lombok and Jackson annotations for convenience.
 */
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
