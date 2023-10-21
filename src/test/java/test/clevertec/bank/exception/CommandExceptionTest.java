package test.clevertec.bank.exception;

import by.clevertec.bank.exception.CommandException;
import by.clevertec.bank.exception.DaoException;
import by.clevertec.bank.exception.ServiceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

public class CommandExceptionTest {
    @Test
    void shouldGetHttpCodeFromServiceException() {
        Assertions.assertThatException()
                .isThrownBy(() -> {
                    throw new CommandException(new ServiceException("test", 123));
                }).isExactlyInstanceOf(CommandException.class)
                .asInstanceOf(InstanceOfAssertFactories.throwable(CommandException.class))
                .extracting(CommandException::getHttpCode).isEqualTo(123);
    }

    @Test
    void shouldNotGetHttpCodeFromException() {
        Assertions.assertThatException()
                .isThrownBy(() -> {
                    throw new CommandException(new DaoException());
                }).isExactlyInstanceOf(CommandException.class)
                .asInstanceOf(InstanceOfAssertFactories.throwable(CommandException.class))
                .extracting(CommandException::getHttpCode).isEqualTo(500);
    }
}
