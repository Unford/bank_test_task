package by.clevertec.bank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.text.SimpleDateFormat;

public final class DataMapper {
    private DataMapper() {
    }

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("dd-MM-yyyy hh:mm"));
    private static final ModelMapper modelMapper;

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setCollectionsMergeEnabled(false)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }


    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ModelMapper getModelMapper() {
        return modelMapper;
    }

}
