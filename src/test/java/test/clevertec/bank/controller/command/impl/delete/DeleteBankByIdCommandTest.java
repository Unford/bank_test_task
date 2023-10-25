package test.clevertec.bank.controller.command.impl.delete;

import by.clevertec.bank.controller.command.impl.delete.DeleteBankByIdCommand;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.impl.BankServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class DeleteBankByIdCommandTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private BankServiceImpl service;

    @Test
    void shouldThrowCommandException() throws ServiceException, CommandException {
        DeleteBankByIdCommand command = new DeleteBankByIdCommand();
        Mockito.when(request.getParameter(Mockito.any())).thenReturn("1");
        Mockito.when(service.deleteById(Mockito.anyLong()))
                .thenThrow(new ServiceException("test", CustomError.CONFLICT));

        CommandException commandException = Assertions.assertThrows(CommandException.class,
                () -> command.execute(request, service));
        assertThat(commandException).hasMessageEndingWith("test");
        assertThat(commandException.getHttpCode()).isEqualTo(CustomError.CONFLICT);
        Mockito.verify(service, Mockito.times(1)).deleteById(Mockito.anyLong());


    }
}
