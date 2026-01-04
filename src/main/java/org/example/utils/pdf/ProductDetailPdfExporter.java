package org.example.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.example.dto.response.Product.ProductDetailResponse;

import java.io.FileOutputStream;

/**
 * Xuất PDF chi tiết 1 sản phẩm.
 * Dùng cho in ấn / xem chi tiết, KHÔNG dùng cho danh sách.
 */
public class ProductDetailPdfExporter {

    private ProductDetailPdfExporter() {}

    public static void export(
            ProductDetailResponse p,
            String outputPath
    ) {
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // ===== TITLE =====
            Paragraph title = new Paragraph(
                    "CHI TIẾT SẢN PHẨM",
                    PdfStyles.TITLE_FONT
            );
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // ===== CONTENT =====
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            addRow(table, "Mã sản phẩm", p.getCode());
            addRow(table, "Tên sản phẩm", p.getName());
            addRow(table,"BarCode",p.getBarcode());
            addRow(table, "Danh mục", p.getCategoryName());
            addRow(table, "Nhà sản xuất", p.getManufacturerName());
            addRow(table,"Giá bán",p.getStandardPrice().toString());
            addRow(table, "Đơn vị", p.getUnit());
            addRow(table, "Trạng thái", p.getStatus().name());
            addRow(table, "Mô tả", p.getDescription());

            document.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Export product detail PDF failed", e);
        } finally {
            document.close();
        }
    }

    // ===== HELPER =====
    private static void addRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(
                new Phrase(label, PdfStyles.HEADER_FONT)
        );
        labelCell.setBackgroundColor(PdfStyles.HEADER_BG);
        labelCell.setPadding(9);

        PdfPCell valueCell = new PdfPCell(
                new Phrase(safe(value), PdfStyles.BODY_FONT)
        );
        valueCell.setPadding(9);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private static String safe(String v) {
        return v == null ? "" : v;
    }
}
