/**
 * The PdfFileUtils class provides methods for generating and saving PDF files for bank transactions and account
 * statements.
 * <p>
 * The PdfFileUtils class provides methods for generating and saving PDF files for bank transactions and account
 * statements.
 * <p>
 * The PdfFileUtils class provides methods for generating and saving PDF files for bank transactions and account
 * statements.
 * <p>
 * The PdfFileUtils class provides methods for generating and saving PDF files for bank transactions and account
 * statements.
 */
/**
 * The PdfFileUtils class provides methods for generating and saving PDF files for bank transactions and account
 * statements.
 */
package by.clevertec.bank.util;

import by.clevertec.bank.model.domain.AccountTransaction;
import by.clevertec.bank.model.dto.AccountExtractDto;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class PdfFileUtils {
    private static final Logger logger = LogManager.getLogger();
    private static final String CHECK_PATH = "check";
    private static final String ACCOUNT_EXTRACT_PATH = "bank-extract";

    private static final String CHECK_PATH_PATTERN = CHECK_PATH + "/check_#-%d.pdf";
    private static final String ACCOUNT_EXTRACT_PATTERN = ACCOUNT_EXTRACT_PATH + "/%s-extract-%s-%s.pdf";


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


    public static void saveCheck(AccountTransaction transaction) {
        File directory = new File(CHECK_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(CHECK_PATH_PATTERN.formatted(transaction.getId())));
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


    }


    public static void saveAccountExtract(AccountExtractDto statementDto) {
        File directory = new File(ACCOUNT_EXTRACT_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(ACCOUNT_EXTRACT_PATTERN
                    .formatted(statementDto.getAccount().getId(),
                            statementDto.getFrom().toString(),
                            statementDto.getTo().toString()
                    )));
            document.open();
            PdfPTable table = new PdfPTable(2);
            setHeader("Account extract", table);
            PdfPCell bankCell = new PdfPCell();

            bankCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            bankCell.setColspan(2);
            bankCell.setPhrase(new Phrase(statementDto.getAccount().getBank().getName()));
            table.addCell(bankCell);

            table.addCell("Client");
            table.addCell(statementDto.getAccount().getOwner().getFullName());

            table.addCell("Account");
            table.addCell(statementDto.getAccount().getAccount());

            table.addCell("Currency");
            table.addCell("BYN");

            table.addCell("Open date");
            table.addCell(statementDto.getAccount().getOpenDate().toString());

            table.addCell("Period");
            table.addCell("%s - %s".formatted(statementDto.getFrom().toString(), statementDto.getTo().toString()));

            table.addCell("Report date");
            table.addCell(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH.mm")));

            table.addCell("Account balance");
            table.addCell("%s BYN".formatted(statementDto.getBalance().toString()));
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


    }

}
