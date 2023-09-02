package by.clevertec.bank.model.dto;

import by.clevertec.bank.model.validation.CreateAccountValidationGroup;
import by.clevertec.bank.model.validation.CreateValidationGroup;
import by.clevertec.bank.model.validation.UpdateValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The UserDto class is a data transfer object that represents a user with an ID and a full name, and includes annotations
 * for validation and serialization.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotNull(groups = {CreateAccountValidationGroup.class, UpdateValidationGroup.class})
    private Long id;
    @NotBlank(groups = CreateValidationGroup.class)
    private String fullName;

}
