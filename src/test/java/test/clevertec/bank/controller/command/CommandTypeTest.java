package test.clevertec.bank.controller.command;

import by.clevertec.bank.controller.HttpMethod;
import by.clevertec.bank.controller.ServletPath;
import by.clevertec.bank.controller.command.Command;
import by.clevertec.bank.controller.command.CommandType;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommandTypeTest {
    @Mock
    HttpServletRequest request;
    @Test
    void shouldReturnDefaultCommandIfUnknown() {
        Command expected = CommandType.DEFAULT_COMMAND.getCommand();
        Command actual = CommandType.defineCommand("some text", request);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnDefaultCommandIfInvalidMethod() {
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.PUT);
        Mockito.when(request.getServletPath()).thenReturn(ServletPath.ACCOUNT);

        Command expected = CommandType.DEFAULT_COMMAND.getCommand();
        Command actual = CommandType.defineCommand(CommandType.CREATE_ACCOUNT.name(), request);
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(request, Mockito.times(2)).getMethod();
        Mockito.verify(request, Mockito.times(2)).getServletPath();

    }

    @Test
    void shouldReturnDefaultCommandIfInvalidPath() {
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.POST);
        Mockito.when(request.getServletPath()).thenReturn(ServletPath.BANK);

        Command expected = CommandType.DEFAULT_COMMAND.getCommand();
        Command actual = CommandType.defineCommand(CommandType.CREATE_ACCOUNT.name(), request);
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(request, Mockito.times(1)).getMethod();
        Mockito.verify(request, Mockito.times(2)).getServletPath();
    }

    @Test
    void shouldReturnCommandByMethodAndPath() {
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.POST);
        Mockito.when(request.getServletPath()).thenReturn(ServletPath.ACCOUNT);

        Command expected = CommandType.CREATE_ACCOUNT.getCommand();
        Command actual = CommandType.defineCommand(CommandType.CREATE_ACCOUNT.name(), request);
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(request, Mockito.times(2)).getMethod();
        Mockito.verify(request, Mockito.times(2)).getServletPath();
    }
}
