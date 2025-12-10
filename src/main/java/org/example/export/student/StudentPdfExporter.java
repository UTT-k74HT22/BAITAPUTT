package org.example.export.student;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.example.entity.Student;
import org.example.export.base.BasePdfExporter;

import java.util.List;

public class StudentPdfExporter extends BasePdfExporter<Student> {

    @Override
    protected Paragraph createTitle() {
        return new Paragraph("DANH SÁCH NHÂN VIÊN")
                .setFont(boldFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER);
    }

    @Override
    protected Table createTable(List<Student> data) {
        float[] columnWidths = {30f, 120f, 80f, 120f, 60f, 80f, 140f, 80f};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));

        // Header
        table.addHeaderCell(createHeaderCell("STT"));
        table.addHeaderCell(createHeaderCell("Họ tên"));
        table.addHeaderCell(createHeaderCell("Ngày sinh"));
        table.addHeaderCell(createHeaderCell("Địa chỉ"));
        table.addHeaderCell(createHeaderCell("Giới tính"));
        table.addHeaderCell(createHeaderCell("Mã NV"));
        table.addHeaderCell(createHeaderCell("Email"));
        table.addHeaderCell(createHeaderCell("Điểm tổng kết"));

        int stt = 1;
        for (Student sv : data) {
            table.addCell(createBodyCell(String.valueOf(stt++)));
            table.addCell(createBodyCell(sv.getHoTen()));
            table.addCell(createBodyCell(sv.getNgaySinh()));
            table.addCell(createBodyCell(sv.getDiaChi()));
            table.addCell(createBodyCell(sv.getGioiTinh()));
            table.addCell(createBodyCell(sv.getMaSinhVien()));
            table.addCell(createBodyCell(sv.getEmail()));
            table.addCell(createBodyCell(
                    sv.getDiemTongKet() == null ? "" : String.valueOf(sv.getDiemTongKet())
            ));
        }

        return table;
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFont(boldFont))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createBodyCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setTextAlignment(TextAlignment.LEFT);
    }
}
