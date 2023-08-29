package by.clevertec.bank.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomError {
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL = 500;

    private int code;
    private String message;
}
