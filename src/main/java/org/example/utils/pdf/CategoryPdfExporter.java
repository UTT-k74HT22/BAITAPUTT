package org.example.utils.pdf;

import org.example.dto.response.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF exporter cho CategoryResponse.
 * Nhẹ hơn Excel, dùng chung PdfExporter.
 */
public class CategoryPdfExporter {

    private CategoryPdfExporter() {}

    public static void export(
            List<CategoryResponse> data,
            String outputPath
    ) {
        PdfExporter.exportTable(
                title(),
                headers(),
                rows(data),
                outputPath
        );
    }

    // ===== META =====
    private static String title() {
        return "DANH SÁCH DANH MỤC";
    }

    private static String[] headers() {
        return new String[]{
                "STT",
                "Mã danh mục",
                "Tên danh mục",
                "Danh mục cha",
                "Mô tả",
                "Trạng thái",
                "Thứ tự hiển thị"
        };
    }

    // ===== DATA =====
    private static List<String[]> rows(List<CategoryResponse> data) {
        List<String[]> rows = new ArrayList<>();
        int stt = 1;

        for (CategoryResponse c : data) {
            rows.add(new String[]{
                    String.valueOf(stt++),
                    safe(c.getCode()),
                    safe(c.getName()),
                    safe(c.getParentName()),
                    safe(c.getDescription()),
                    c.isActive() ? "ACTIVE" : "INACTIVE",
                    String.valueOf(c.getDisplayOrder())
            });
        }
        return rows;
    }

    private static String safe(Object o) {
        return o == null ? "" : o.toString();
    }
}
