
package by.clevertec.bank.util;

import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.AccountDto;
import by.clevertec.bank.model.dto.AccountExtractDto;
import by.clevertec.bank.model.dto.AccountStatementDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The PdfFileUtils class provides methods for saving different types of PDF files related to bank transactions.
 */
public final class PdfFileUtils {
    private static final Logger logger = LogManager.getLogger();
    public static final String CHECK_PATH = "check";
    public static final String ACCOUNT_EXTRACT_PATH = "bank-extract";

    public static final String ACCOUNT_STATEMENT_PATH = "statement-money";

    private static final String CHECK_PATH_PATTERN = CHECK_PATH + "/check_#-%d.pdf";
    private static final String ACCOUNT_EXTRACT_PATTERN = ACCOUNT_EXTRACT_PATH + "/%s-extract-%s-%s.pdf";
    private static final String ACCOUNT_STATEMENT_PATTERN = ACCOUNT_STATEMENT_PATH +
            "/%s-" + ACCOUNT_STATEMENT_PATH + "-%s-%s.pdf";


    private PdfFileUtils() {
    }

    private static PdfPCell getCenteredCell(String phrase) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(phrase));
        return cell;
    }

    private static void setHeader(String phrase, PdfPTable table) {
        PdfPCell header = getCenteredCell(phrase);
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setColspan(2);
        table.addCell(header);
    }


    /**
     * The function saves an account transaction as a PDF check file.
     *
     * @param transaction The "transaction" parameter is an instance of the "AccountTransaction" class. It represents a
     *                    transaction made between two bank accounts.
     */
    public static File saveCheck(AccountTransaction transaction) {
        File directory = new File(CHECK_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(CHECK_PATH_PATTERN.formatted(transaction.getId()));
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            PdfPTable table = new PdfPTable(2);
            setHeader("Bank check", table);
            String type = transaction.getSum().signum() < 0 ? "Withdrawal"
                    : transaction.getFrom().getId() != 0 ? "Transfer" : "Top-up";

            table.addCell("Check number:");
            table.addCell(transaction.getId().toString());
            table.addCell(transaction.getDateTime().toLocalDate().toString());
            String time = transaction.getDateTime().toLocalTime()
                    .format(DateTimeFormatter.ofPattern("hh:mm:ss"));
            table.addCell(time);
            table.addCell("Type of transaction:");
            table.addCell(type);
            if (transaction.getFrom().getId() != 0) {
                table.addCell("Senders bank:");
                table.addCell(transaction.getFrom().getBank().getName());
                table.addCell("Sender Account:");
                table.addCell(transaction.getFrom().getAccount());
            }
            table.addCell("Receiver bank:");
            table.addCell(transaction.getTo().getBank().getName());
            table.addCell("Receiver Account:");
            table.addCell(transaction.getTo().getAccount());
            table.addCell("SUM:");
            table.addCell(transaction.getSum().toString() + " BYN");

            document.add(table);

        } catch (DocumentException | FileNotFoundException e) {
            logger.error("Can not save check file {}", transaction.getId(), e);
        } finally {
            document.close();
        }
        return file;

    }


    private static PdfPTable createAccountInfoTable(String header, AccountDto account,
                                                    LocalDate from, LocalDate to, BigDecimal balance) {
        PdfPTable table = new PdfPTable(2);
        setHeader(header, table);
        PdfPCell bankCell = new PdfPCell();
        bankCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankCell.setColspan(2);
        bankCell.setPhrase(new Phrase(account.getBank().getName()));
        table.addCell(bankCell);

        table.addCell("Client");
        table.addCell(account.getUser().getFullName());

        table.addCell("Account");
        table.addCell(account.getAccount());

        table.addCell("Currency");
        table.addCell("BYN");

        table.addCell("Open date");
        table.addCell(account.getOpenDate().toString());

        table.addCell("Period");
        table.addCell("%s - %s".formatted(from.toString(), to.toString()));

        table.addCell("Report date");
        table.addCell(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH.mm")));

        table.addCell("Account balance");
        table.addCell("%s BYN".formatted(balance.toString()));
        return table;
    }


    /**
     * The function `saveAccountExtract` saves an account extract as a PDF file, including account information and
     * transaction details.
     *
     * @param statementDto The `statementDto` parameter is an object of type `AccountExtractDto`. It contains information
     *                     about the account extract, including the account details, date range, balance, and a list of transactions.
     */
    public static File saveAccountExtract(AccountExtractDto statementDto) {
        File directory = new File(ACCOUNT_EXTRACT_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }

        Document document = new Document();
        File file = new File(ACCOUNT_EXTRACT_PATTERN
                .formatted(statementDto.getAccount().getId(),
                        statementDto.getFrom().toString(),
                        statementDto.getTo().toString()
                ));

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            PdfPTable table = createAccountInfoTable("Account extract", statementDto.getAccount(),
                    statementDto.getFrom(), statementDto.getTo(), statementDto.getBalance());
            document.add(table);

            PdfPTable subTable = new PdfPTable(3);
            subTable.setPaddingTop(40.f);
            subTable.addCell(getCenteredCell("Date"));
            subTable.addCell(getCenteredCell("Note"));
            subTable.addCell(getCenteredCell("Sum"));

            if (statementDto.getTransactions().isEmpty()) {
                PdfPCell emptyCell = getCenteredCell("No transactions");
                emptyCell.setColspan(3);
                subTable.addCell(emptyCell);
            } else {
                statementDto.getTransactions().forEach(t -> {
                    BigDecimal s = t.getSum();
                    subTable.addCell(getCenteredCell(t.getDateTime().toLocalDate().toString()));
                    String note;
                    if (t.getSum().signum() < 0) {
                        note = "Withdrawal";
                    } else {
                        if (t.getFrom().getId() != 0) {
                            if (t.getFrom().getId().equals(statementDto.getAccount().getId())) {
                                s = s.negate();
                                note = "Transfer to %d".formatted(t.getTo().getId());
                            } else {
                                note = "Transfer from %d".formatted(t.getFrom().getId());
                            }
                        } else {
                            note = "Top-up";
                        }
                    }
                    subTable.addCell(getCenteredCell(note));
                    subTable.addCell(getCenteredCell("%s BYN".formatted(s.toString())));

                });
            }


            document.add(subTable);

        } catch (DocumentException | FileNotFoundException e) {
            logger.error("Can not save account extract file", e);
        } finally {
            document.close();
        }
        return file;

    }

    /**
     * The function `saveAccountStatement` saves an account statement as a PDF file.
     *
     * @param statementDto The `statementDto` parameter is an object of type `AccountStatementDto`. It contains information
     *                     about the account statement that needs to be saved. The `AccountStatementDto` class likely has properties such as
     *                     `account`, `from`, `to`, and `money`, which hold the relevant data for
     */
    public static File saveAccountStatement(AccountStatementDto statementDto) {
        File directory = new File(ACCOUNT_STATEMENT_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }

        Document document = new Document();
        File file = new File(ACCOUNT_STATEMENT_PATTERN
                .formatted(statementDto.getAccount().getId(),
                        statementDto.getFrom().toString(),
                        statementDto.getTo().toString()
                ));
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            PdfPTable table = createAccountInfoTable("Money statement", statementDto.getAccount(),
                    statementDto.getFrom(), statementDto.getTo(), statementDto.getMoney().getBalance());
            document.add(table);

            PdfPTable subTable = new PdfPTable(2);
            subTable.setWidthPercentage(50f);
            subTable.setPaddingTop(40.f);
            subTable.addCell(getCenteredCell("Income"));
            subTable.addCell(getCenteredCell("Expenditure"));
            subTable.addCell(getCenteredCell("%s BYN".formatted(statementDto.getMoney().getIncome().toString())));
            subTable.addCell(getCenteredCell("%s BYN".formatted(statementDto.getMoney().getExpenditure().toString())));
            document.add(subTable);

        } catch (DocumentException | FileNotFoundException e) {
            logger.error("Can not save account statement file", e);
        } finally {
            document.close();
        }
        return file;
    }
}
