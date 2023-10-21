package test.clevertec.bank.controller.command.impl.get;

import by.clevertec.bank.controller.command.impl.get.GetAccountByIdCommand;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GetAccountByIdCommandTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private AccountServiceImpl service;

    @Test
    void shouldThrowCommandException() throws ServiceException, CommandException {
        GetAccountByIdCommand command = new GetAccountByIdCommand();
        Mockito.when(request.getParameter(Mockito.any())).thenReturn("1");
        Mockito.when(service.findById(Mockito.anyLong()))
                .thenThrow(new ServiceException("test", CustomError.CONFLICT));

        CommandException commandException = Assertions.assertThrows(CommandException.class,
                () -> command.execute(request, service));
        assertThat(commandException).hasMessageEndingWith("test");
        assertThat(commandException.getHttpCode()).isEqualTo(CustomError.CONFLICT);
        Mockito.verify(service, Mockito.times(1)).findById(Mockito.anyLong());


    }
}
