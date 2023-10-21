package test.clevertec.bank.controller.listener;

import by.clevertec.bank.config.AppConfiguration;
import by.clevertec.bank.controller.listener.ServletContextListenerImpl;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccrualTaskTest {
    @Mock
    private AccountServiceImpl service;

    @Mock
    private AppConfiguration appConfiguration;

    @Test
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
