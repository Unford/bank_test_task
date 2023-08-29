package by.clevertec.bank.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Jacksonized
public class AccountDto {
    @NotNull
    private Long id;
    private String account;
}
