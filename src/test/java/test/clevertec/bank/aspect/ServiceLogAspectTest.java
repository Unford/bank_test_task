package test.clevertec.bank.aspect;

import by.clevertec.bank.aspect.ServiceLogAspect;
import by.clevertec.bank.exception.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
 class ServiceLogAspectTest {
    @Mock
    ProceedingJoinPoint joinPoint;
    @Mock
    Signature signature;

    @Test
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
