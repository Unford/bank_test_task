package test.clevertec.bank.controller.listener;

import by.clevertec.bank.config.AppConfiguration;
import by.clevertec.bank.controller.listener.ServletContextListenerImpl;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class AccrualTaskTest {
    @Mock
    private AccountServiceImpl service;

    @Mock
    private AppConfiguration appConfiguration;

    @Test
    @DisplayName("Run AccrualTask with ServiceException")
    void shouldRunAccrualTaskWithServiceException() throws ServiceException {
        ServletContextListenerImpl.AccrualTask task =
                new ServletContextListenerImpl.AccrualTask(service);

        Mockito.doThrow(new ServiceException("test", CustomError.INTERNAL)).when(service).accrueIncome(Mockito.anyInt());
        Mockito.when(appConfiguration.getBusiness()).thenReturn(new AppConfiguration.BusinessConfig());

        try (MockedStatic<AppConfiguration> config = Mockito.mockStatic(AppConfiguration.class)) {
            config.when(AppConfiguration::getInstance).thenReturn(appConfiguration);
            task.run();
            Mockito.verify(service, Mockito.times(1)).accrueIncome(Mockito.anyInt());
            config.verify(AppConfiguration::getInstance, Mockito.times(1));

        }

    }

    @Test
    @DisplayName("Should run AccrualTask and accrue income once")
    void shouldRunAccrualTask() throws ServiceException {
        ServletContextListenerImpl.AccrualTask task =
                new ServletContextListenerImpl.AccrualTask(service);

        Mockito.doNothing().when(service).accrueIncome(Mockito.anyInt());
        Mockito.when(appConfiguration.getBusiness()).thenReturn(new AppConfiguration.BusinessConfig());

        try (MockedStatic<AppConfiguration> config = Mockito.mockStatic(AppConfiguration.class)) {
            config.when(AppConfiguration::getInstance).thenReturn(appConfiguration);
            task.run();
            Mockito.verify(service, Mockito.times(1)).accrueIncome(Mockito.anyInt());
            config.verify(AppConfiguration::getInstance, Mockito.times(1));

        }

    }
}
