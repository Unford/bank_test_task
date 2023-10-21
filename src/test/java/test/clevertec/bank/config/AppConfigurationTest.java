package test.clevertec.bank.config;

import by.clevertec.bank.config.AppConfiguration;
import by.clevertec.bank.exception.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ExtendWith(MockitoExtension.class)
class AppConfigurationTest {
    @Test
    void shouldCreateOnlyOneInstance() throws ServiceException, ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<AppConfiguration> first = executorService.submit(AppConfiguration::getInstance);
        Future<AppConfiguration> second = executorService.submit(AppConfiguration::getInstance);

        Assertions.assertEquals(first.get(), AppConfiguration.getInstance());
        Assertions.assertEquals(second.get(), AppConfiguration.getInstance());


    }
}
