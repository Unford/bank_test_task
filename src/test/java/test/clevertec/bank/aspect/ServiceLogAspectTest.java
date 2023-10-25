package test.clevertec.bank.aspect;

import by.clevertec.bank.aspect.ServiceLogAspect;
import by.clevertec.bank.exception.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
 class ServiceLogAspectTest {
    @Mock
    ProceedingJoinPoint joinPoint;
    @Mock
    Signature signature;

    @Test
    @DisplayName("Should log method call and return result")
    void shouldLogMethodCallReturnResult() throws Throwable {
        String expected = "res";
        ServiceLogAspect aspect = new ServiceLogAspect();
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(signature.getDeclaringType()).thenReturn(String.class);
        Mockito.when(signature.getName()).thenReturn("Method_name");
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{1, "two"});
        Mockito.when(joinPoint.proceed()).thenReturn(expected);


        aspect.executeLogging();
        var actual = aspect.logMethodCall(joinPoint);
        Assertions.assertThat(actual).isEqualTo(expected);



    }

    @Test
    @DisplayName("Should log method call with no args and return result")
    void shouldLogMethodCallWithNoArgsReturnResult() throws Throwable {
        String expected = "res";
        ServiceLogAspect aspect = new ServiceLogAspect();
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(signature.getDeclaringType()).thenReturn(String.class);
        Mockito.when(signature.getName()).thenReturn("Method_name");
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{});
        Mockito.when(joinPoint.proceed()).thenReturn(expected);


        aspect.executeLogging();
        var actual = aspect.logMethodCall(joinPoint);
        Assertions.assertThat(actual).isEqualTo(expected);



    }

    @Test
    @DisplayName("Should log method call with null args and return result")
    void shouldLogMethodCallWithNullArgsReturnResult() throws Throwable {
        String expected = "res";
        ServiceLogAspect aspect = new ServiceLogAspect();
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(signature.getDeclaringType()).thenReturn(String.class);
        Mockito.when(signature.getName()).thenReturn("Method_name");
        Mockito.when(joinPoint.getArgs()).thenReturn(null);
        Mockito.when(joinPoint.proceed()).thenReturn(expected);


        aspect.executeLogging();
        var actual = aspect.logMethodCall(joinPoint);
        Assertions.assertThat(actual).isEqualTo(expected);



    }
    @Test
    @DisplayName("Should log method call with throwable inside")
    void shouldLogMethodCallWithThrowableInside() throws Throwable {
        ServiceException expected = new ServiceException();
        ServiceLogAspect aspect = new ServiceLogAspect();
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(signature.getDeclaringType()).thenReturn(String.class);
        Mockito.when(signature.getName()).thenReturn("Method_name");
        Mockito.when(joinPoint.getArgs()).thenReturn(null);
        Mockito.when(joinPoint.proceed()).thenThrow(expected);

        aspect.executeLogging();


        Exception exception = Assertions.catchException(() -> aspect.logMethodCall(joinPoint));
        Assertions.assertThat(exception).message().isEqualTo(expected.getMessage());


    }
}
