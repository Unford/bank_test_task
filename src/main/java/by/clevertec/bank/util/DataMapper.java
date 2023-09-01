package by.clevertec.bank.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.text.SimpleDateFormat;

/**
 * The DataMapper class provides static methods to access and configure ObjectMapper and ModelMapper objects for data
 * mapping and serialization/deserialization operations in Java.
 */
public final class DataMapper {
    private DataMapper() {
    }

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .setDateFormat(new SimpleDateFormat("dd-MM-yyyy hh:mm"))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);


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


    /**
     * The function returns an instance of the ObjectMapper class.
     *
     * @return The method is returning an instance of the ObjectMapper class.
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * The function returns an instance of the ModelMapper class.
     *
     * @return The method is returning an instance of the ModelMapper class.
     */
    public static ModelMapper getModelMapper() {
        return modelMapper;
    }

}
