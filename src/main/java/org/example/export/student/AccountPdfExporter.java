package org.example.export.student;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.example.entity.Account;
import org.example.export.base.BasePdfExporter;

import java.util.List;

public class AccountPdfExporter extends BasePdfExporter<Account> {

    @Override
    protected Paragraph createTitle() {
        return new Paragraph("DANH SÁCH TÀI KHOẢN")
                .setFont(boldFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER);
    }

    @Override
    protected Table createTable(List<Account> data) {
        float[] columnWidths = {30f, 120f, 160f, 80f, 80f};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));

        // Header
        table.addHeaderCell(createHeaderCell("STT"));
        table.addHeaderCell(createHeaderCell("Username"));
        table.addHeaderCell(createHeaderCell("Họ tên"));
        table.addHeaderCell(createHeaderCell("Role"));
        table.addHeaderCell(createHeaderCell("Trạng thái"));

        int stt = 1;
        for (Account acc : data) {
            table.addCell(createBodyCell(String.valueOf(stt++)));
            table.addCell(createBodyCell(acc.getUsername()));
            table.addCell(createBodyCell(acc.getFullName()));
            table.addCell(createBodyCell(acc.getRole()));
            table.addCell(createBodyCell(acc.isActive() ? "Active" : "Inactive"));
        }

        return table;
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFont(boldFont))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createBodyCell(String text) {
        String safeText = (text == null) ? "" : text;
        return new Cell()
                .add(new Paragraph(safeText).setFont(font))
                .setTextAlignment(TextAlignment.LEFT);
    }
}
