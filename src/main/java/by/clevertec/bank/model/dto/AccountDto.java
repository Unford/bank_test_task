package by.clevertec.bank.model.dto;

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
public class AccountDto {
    @NotNull
    private Long id;
    private String account;
}
