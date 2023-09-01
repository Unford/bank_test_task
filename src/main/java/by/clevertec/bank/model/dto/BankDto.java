package by.clevertec.bank.model.dto;

import by.clevertec.bank.model.validation.CreateValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

/**
 * The BankDto class is a data transfer object that represents a bank and includes an id and name.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class BankDto {
    @NotNull(groups = CreateValidationGroup.class)
    private Long id;
    private String name;
}
