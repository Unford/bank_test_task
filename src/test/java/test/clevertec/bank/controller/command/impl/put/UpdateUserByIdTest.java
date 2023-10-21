package test.clevertec.bank.controller.command.impl.put;

import by.clevertec.bank.controller.command.impl.put.UpdateUserByIdCommand;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.gen.DataGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UpdateUserByIdTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private UserServiceImpl service;

    @Test
    void shouldThrowCommandException() throws ServiceException, CommandException {
        UpdateUserByIdCommand command = Mockito.spy(UpdateUserByIdCommand.class);
        Mockito.doReturn(DataGenerator.generateUserDto())
                .when(command).readBody(Mockito.any(), Mockito.any());


        Mockito.when(service.update(Mockito.any())).thenThrow(new ServiceException("test", CustomError.CONFLICT));

        CommandException commandException = Assertions.assertThrows(CommandException.class,
                () -> command.execute(request, service));
        assertThat(commandException).hasMessageEndingWith("test");
        assertThat(commandException.getHttpCode()).isEqualTo(CustomError.CONFLICT);
        Mockito.verify(service, Mockito.times(1)).update(Mockito.any());

    }
}
