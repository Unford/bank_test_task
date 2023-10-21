package test.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.impl.post.TransferCommand;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
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
class TransferCommandTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private AccountTransactionServiceImpl service;

    @Test
    void shouldThrowCommandExceptionFromService() throws ServiceException, CommandException {
        TransferCommand command = Mockito.spy(TransferCommand.class);
        TransactionDto dto = DataGenerator.generateTransactionDto();
        dto.getTo().setId(1L);
        dto.getFrom().setId(2L);
        Mockito.doReturn(dto)
                .when(command).readBody(Mockito.any(), Mockito.any());
        Mockito.when(service.transferMoney(Mockito.any()))
                .thenThrow(new ServiceException("test", CustomError.CONFLICT));

        CommandException commandException = Assertions.assertThrows(CommandException.class,
                () -> command.execute(request, service));
        assertThat(commandException).hasMessageEndingWith("test");
        assertThat(commandException.getHttpCode()).isEqualTo(CustomError.CONFLICT);
        Mockito.verify(service, Mockito.times(1)).transferMoney(Mockito.any());

    }

    @Test
    void shouldThrowCommandExceptionWhenIdsEquals() throws ServiceException, CommandException {
        TransferCommand command = Mockito.spy(TransferCommand.class);
        TransactionDto dto = DataGenerator.generateTransactionDto();
        dto.getTo().setId(1L);
        dto.getFrom().setId(1L);
        Mockito.doReturn(dto)
                .when(command).readBody(Mockito.any(), Mockito.any());
        CommandException commandException = Assertions.assertThrows(CommandException.class,
                () -> command.execute(request, service));
        assertThat(commandException).message().isNotBlank();
        assertThat(commandException.getHttpCode()).isEqualTo(CustomError.BAD_REQUEST);

    }
}
