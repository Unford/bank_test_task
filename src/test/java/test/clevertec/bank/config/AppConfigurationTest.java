package test.clevertec.bank.config;

import by.clevertec.bank.config.AppConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class AppConfigurationTest {
    @Test
    @DisplayName("Ensures Only One AppConfiguration Instance is Created")
    void shouldCreateOnlyOneInstance() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<AppConfiguration> first = executorService.submit(AppConfiguration::getInstance);
        Future<AppConfiguration> second = executorService.submit(AppConfiguration::getInstance);

        Assertions.assertEquals(first.get(), AppConfiguration.getInstance());
        Assertions.assertEquals(second.get(), AppConfiguration.getInstance());


    }
}
