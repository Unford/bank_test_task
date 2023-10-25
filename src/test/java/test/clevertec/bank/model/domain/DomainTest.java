package test.clevertec.bank.model.domain;

import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;
import test.clevertec.bank.common.DataGenerator;

@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
 class DomainTest {
    @Test
    void shouldCreateAccountByAllArgsConstructor(){
        Account expected = DataGenerator.generateAccount();
        Account actual = new Account(expected.getId(),
                expected.getAccount(), expected.getOpenDate(),
                expected.getLastAccrualDate(), expected.getBank(), expected.getUser());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

     @Test
     void shouldCreateAccountTransactionByAllArgsConstructor(){
         AccountTransaction expected = DataGenerator.generateTransaction();
         AccountTransaction actual = new AccountTransaction(expected.getId(),
                 expected.getSum(), expected.getDateTime(),
                 expected.getTo(), expected.getFrom());

         Assertions.assertThat(actual).isEqualTo(expected);
     }
}
