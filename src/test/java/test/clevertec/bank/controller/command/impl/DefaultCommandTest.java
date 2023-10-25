package test.clevertec.bank.controller.command.impl;

import by.clevertec.bank.controller.command.impl.DefaultCommand;
import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.service.CrudService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;


@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class DefaultCommandTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private CrudService service;

    @Test
    void shouldThrowCommandException() {
        DefaultCommand defaultCommand = new DefaultCommand();
        CommandException commandException = Assertions.assertThrows(CommandException.class,
                () -> defaultCommand.execute(request, service));
        Assertions.assertEquals("Unknown command", commandException.getMessage());
    }
}
