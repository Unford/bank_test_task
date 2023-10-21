package test.clevertec.bank.gen;

import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.domain.Bank;
import by.clevertec.bank.model.domain.User;
import by.clevertec.bank.model.dto.*;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public final class DataGenerator {
    private DataGenerator() {
    }

    private static final Faker faker = new Faker();

    public static AccountDto generateAccountDto() {
        return AccountDto.builder()
                .id((long) faker.number().positive())
                .account(faker.finance().creditCard())
                .lastAccrualDate(faker.date().birthday().toLocalDateTime().toLocalDate())
                .openDate(faker.date().birthday().toLocalDateTime().toLocalDate())
                .user(generateUserDto())
                .bank(generateBankDto())
                .build();
    }

    public static Account generateAccount() {
        return Account.builder()
                .id((long) faker.number().positive())
                .account(faker.finance().creditCard())
                .lastAccrualDate(faker.date().birthday().toLocalDateTime().toLocalDate())
                .openDate(faker.date().birthday().toLocalDateTime().toLocalDate())
                .user(generateUser())
                .bank(generateBank())
                .build();
    }

    public static User generateUser() {
        return User.builder()
                .id((long) faker.number().positive())
                .fullName(faker.name().fullName())
                .build();
    }

    public static UserDto generateUserDto() {
        return UserDto.builder()
                .id((long) faker.number().positive())
                .fullName(faker.name().fullName())
                .build();
    }

    public static BankDto generateBankDto() {
        return BankDto.builder()
                .id((long) faker.number().positive())
                .name(faker.appliance().brand())
                .build();
    }

    public static Bank generateBank() {
        return Bank.builder()
                .id((long) faker.number().positive())
                .name(faker.appliance().brand())
                .build();
    }

    public static AccountExtractDto generateAccountExtractDto() {
        return AccountExtractDto.builder()
                .account(generateAccountDto())
                .balance(BigDecimal.valueOf(faker.number().positive()))
                .from(faker.date().past(100, TimeUnit.DAYS).toLocalDateTime().toLocalDate())
                .to(faker.date().future(100, TimeUnit.DAYS).toLocalDateTime().toLocalDate())
                .transactions(Stream.generate(DataGenerator::generateTransactionDto)
                        .limit(faker.number().numberBetween(0, 5))
                        .toList())
                .build();
    }

    public static TransactionDto generateTransactionDto() {
        return TransactionDto.builder()
                .id((long) faker.number().positive())
                .dateTime(faker.date().birthday().toLocalDateTime())
                .from(generateAccountDto())
                .to(generateAccountDto())
                .sum(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 1000)))
                .build();
    }

    public static AccountTransaction generateTransaction() {
        return AccountTransaction.builder()
                .id((long) faker.number().positive())
                .dateTime(faker.date().birthday().toLocalDateTime())
                .from(generateAccount())
                .to(generateAccount())
                .sum(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 1000)))
                .build();
    }

    public static AccountStatementDto generateAccountStatementDto() {
        return AccountStatementDto.builder()
                .account(generateAccountDto())
                .from(faker.date().past(100, TimeUnit.DAYS).toLocalDateTime().toLocalDate())
                .to(faker.date().future(100, TimeUnit.DAYS).toLocalDateTime().toLocalDate())
                .money(generateMoneyStatsDto())
                .build();
    }

    public static MoneyStatsDto generateMoneyStatsDto() {
        BigDecimal balance = BigDecimal.valueOf(faker.number().randomDouble(2, 0, 1000));
        BigDecimal expends = balance.subtract(BigDecimal.valueOf(faker.number()
                .randomDouble(2, 0, balance.intValue())));
        return MoneyStatsDto.builder()
                .balance(balance)
                .expenditure(expends)
                .income(balance.subtract(expends))
                .build();
    }


}
