package by.clevertec.bank.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonParser {
    private JsonParser(){}
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return mapper;
    }
}
