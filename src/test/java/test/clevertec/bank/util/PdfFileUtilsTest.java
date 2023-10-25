package test.clevertec.bank.util;

import by.clevertec.bank.model.domain.Account;
import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.AccountExtractDto;
import by.clevertec.bank.model.dto.TransactionDto;
import by.clevertec.bank.util.PdfFileUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import test.clevertec.bank.common.CamelCaseAndUnderscoreNameGenerator;
import test.clevertec.bank.common.DataGenerator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@DisplayNameGeneration(CamelCaseAndUnderscoreNameGenerator.class)
class PdfFileUtilsTest {
    @Nested
    class CheckTest{
        @Test
        void shouldSaveTransferCheck() {
            File actual = PdfFileUtils.saveCheck(DataGenerator.generateTransaction());
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }

        @Test
        void shouldSaveDepositCheck() {
            AccountTransaction transaction = DataGenerator.generateTransaction();
            Account from = new Account();
            from.setId(0L);
            transaction.setFrom(from);
            File actual = PdfFileUtils.saveCheck(transaction);
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }

        @Test
        void shouldSaveWithdrawalCheck() {
            AccountTransaction transaction = DataGenerator.generateTransaction();
            transaction.setSum(BigDecimal.valueOf(-21));
            File actual = PdfFileUtils.saveCheck(transaction);
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }

        @Test
        void shouldSaveTransferCheckIfDirNotExist() throws IOException {
            File dir = new File(PdfFileUtils.CHECK_PATH);
            FileUtils.deleteDirectory(dir);

            File actual = PdfFileUtils.saveCheck(DataGenerator.generateTransaction());
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }

        @Test
        void shouldSaveTransferCheckThrowsDocumentException() throws IOException, DocumentException {

            try (MockedStatic<PdfWriter> writer = Mockito.mockStatic(PdfWriter.class)) {
                writer.when(() -> PdfWriter.getInstance(Mockito.any(), Mockito.any()))
                        .thenThrow(new DocumentException());
                File actual = PdfFileUtils.saveCheck(DataGenerator.generateTransaction());
                Assertions.assertThat(actual).exists().content().isBlank();
                actual.deleteOnExit();

            }

        }
    }

    @Nested
    class ExtractTest{
        @Test
        void shouldSaveAccountExtract() {
            File actual = PdfFileUtils.saveAccountExtract(DataGenerator.generateAccountExtractDto());
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }

        @Test
        void shouldSaveAccountExtractWithoutTransaction() {
            AccountExtractDto dto = DataGenerator.generateAccountExtractDto();
            dto.setTransactions(List.of());
            File actual = PdfFileUtils.saveAccountExtract(dto);
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }

        @Test
        void shouldSaveAccountExtractWithAllTransactionTypes() {
            AccountExtractDto dto = DataGenerator.generateAccountExtractDto();
            AccountDto account = dto.getAccount();
            account.setId(4L);
            dto.setAccount(account);

            TransactionDto withdrawal = DataGenerator.generateTransactionDto();
            withdrawal.setSum(BigDecimal.valueOf(-2));
            TransactionDto deposit = DataGenerator.generateTransactionDto();
            deposit.setFrom(AccountDto.builder().id(0L).build());
            TransactionDto transferTo = DataGenerator.generateTransactionDto();
            transferTo.setFrom(AccountDto.builder().id(account.getId()).build());

            TransactionDto transferFrom = DataGenerator.generateTransactionDto();

            dto.setTransactions(List.of(withdrawal, deposit, transferFrom, transferTo));
            File actual = PdfFileUtils.saveAccountExtract(dto);
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }


        @Test
        void shouldSaveAccountExtractIfDirNotExist() throws IOException {
            File dir = new File(PdfFileUtils.ACCOUNT_EXTRACT_PATH);
            FileUtils.deleteDirectory(dir);

            File actual = PdfFileUtils.saveAccountExtract(DataGenerator.generateAccountExtractDto());
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }

        @Test
        void shouldSaveAccountExtractThrowsDocumentException() throws IOException, DocumentException {

            try (MockedStatic<PdfWriter> writer = Mockito.mockStatic(PdfWriter.class)) {
                writer.when(() -> PdfWriter.getInstance(Mockito.any(), Mockito.any()))
                        .thenThrow(new DocumentException());
                File actual = PdfFileUtils.saveAccountExtract(DataGenerator.generateAccountExtractDto());
                Assertions.assertThat(actual).exists().content().isBlank();
                actual.deleteOnExit();

            }

        }
    }

    @Nested
    class StatementTest{
        @Test
        void shouldSaveAccountStatement() {
            File actual = PdfFileUtils.saveAccountStatement(DataGenerator.generateAccountStatementDto());
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }


        @Test
        void shouldSaveAccountStatementIfDirNotExist() throws IOException {
            File dir = new File(PdfFileUtils.ACCOUNT_STATEMENT_PATH);
            FileUtils.deleteDirectory(dir);

            File actual = PdfFileUtils.saveAccountStatement(DataGenerator.generateAccountStatementDto());
            Assertions.assertThat(actual).exists().content().isNotBlank();
            actual.deleteOnExit();
        }

        @Test
        void shouldSaveBlankAccountStatementThrowsDocumentException() throws IOException, DocumentException {

            try (MockedStatic<PdfWriter> writer = Mockito.mockStatic(PdfWriter.class)) {
                writer.when(() -> PdfWriter.getInstance(Mockito.any(), Mockito.any()))
                        .thenThrow(new DocumentException());
                File actual = PdfFileUtils.saveAccountStatement(DataGenerator.generateAccountStatementDto());
                Assertions.assertThat(actual).exists().content().isBlank();
                actual.deleteOnExit();

            }

        }
    }


}
