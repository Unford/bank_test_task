package by.clevertec.bank.util;

import by.clevertec.bank.model.domain.AccountTransaction;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class PdfFileUtils {
    private static final Logger logger = LogManager.getLogger();
    private static final String CHECK_PATH = "check/%s%d.pdf";
    private PdfFileUtils() {
    }

    public static void saveCheck(AccountTransaction transaction){
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(CHECK_PATH.formatted("check_#",
                    transaction.getId())));
            document.open();

            PdfPTable table = new PdfPTable(2);

            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setColspan(2);
            header.setPhrase(new Phrase("Bank check"));
            table.addCell(header);
            String type = transaction.getSum().signum() < 0 ? "Withdrawal"
                    : transaction.getFrom().getId() != 0 ? "Transfer" : "Top-up";

            table.addCell("Check number:");
            table.addCell(transaction.getId().toString());
            table.addCell(transaction.getDateTime().toLocalDate().toString());
            table.addCell(transaction.getDateTime().toLocalTime().toString());
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
            logger.error("Can not save check file {}", transaction.getId(),e);
        }finally {
            document.close();
        }


    }
}
