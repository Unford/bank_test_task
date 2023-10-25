package test.clevertec.bank.controller.command.impl.post;

import by.clevertec.bank.controller.command.impl.post.WithdrawalCommand;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.ServiceException;
import by.clevertec.bank.model.dto.CustomError;
import by.clevertec.bank.service.impl.AccountTransactionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;
import test.clevertec.bank.common.DataGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class WithdrawalCommandTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private AccountTransactionServiceImpl service;
    @Test
    void shouldThrowCommandException() throws ServiceException, CommandException {
        WithdrawalCommand command = Mockito.spy(WithdrawalCommand.class);
        Mockito.doReturn(DataGenerator.generateTransactionDto())
                .when(command).readBody(Mockito.any(), Mockito.any());
        Mockito.when(service.withdrawal(Mockito.any()))
                .thenThrow(new ServiceException("test", CustomError.CONFLICT));

        CommandException commandException = Assertions.assertThrows(CommandException.class,
                () -> command.execute(request, service));
        assertThat(commandException).hasMessageEndingWith("test");
        assertThat(commandException.getHttpCode()).isEqualTo(CustomError.CONFLICT);
        Mockito.verify(service, Mockito.times(1)).withdrawal(Mockito.any());

    }
}
