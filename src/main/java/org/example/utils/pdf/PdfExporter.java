package org.example.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class PdfExporter {

    public static void exportTable(
            String title,
            String[] headers,
            List<String[]> rows,
            String outputPath
    ) {
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // ===== Title =====
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph pTitle = new Paragraph(title, PdfStyles.TITLE_FONT);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            pTitle.setSpacingAfter(20);
            document.add(pTitle);

            // ===== Table =====
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, PdfStyles.HEADER_FONT));
                cell.setBackgroundColor(PdfStyles.HEADER_BG);
                table.addCell(cell);
            }

            for (String[] row : rows) {
                for (String col : row) {
                    PdfPCell cell = new PdfPCell(new Phrase(col, PdfStyles.BODY_FONT));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);
                }
            }

            document.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Export PDF failed", e);
        } finally {
            document.close();
        }
    }
}
