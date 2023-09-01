package by.clevertec.bank.model.dto;

import by.clevertec.bank.model.validation.CreateValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotNull(groups = CreateValidationGroup.class)
    private Long id;
    private String fullName;

}
