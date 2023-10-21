package by.clevertec.bank.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * The CustomError class is a Java class that represents custom error responses, with predefined error codes and messages.
 */
@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class CustomError {
    public static final int NOT_FOUND = 404;
    public static final int BAD_REQUEST = 400;

    public static final int INTERNAL = 500;
    public static final int CONFLICT = 409;

    private int code;
    private String message;
}
