
package org.example.utils.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.example.dto.response.CategoryResponse;

import java.util.List;

/**
 * Excel writer cho DTO CategoryResponse.
 */
public class CategoryExcelWriter
        extends AbstractExcelWriter<CategoryResponse> {

    @Override
    public String sheetName() {
        return "Categories";
    }

    @Override
    public String title() {
        return "DANH SÁCH DANH MỤC";
    }

    @Override
    public String[] headers() {
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

    @Override
    public void create(Sheet sheet, ExcelStyles styles, List<CategoryResponse> data) {

        // Title
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 600);

        Cell tCell = titleRow.createCell(0);
        tCell.setCellValue(title());
        tCell.setCellStyle(styles.get(StyleKey.TITLE));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers().length - 1));

        // Header
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight((short) 420);

        String[] hs = headers();
        for (int i = 0; i < hs.length; i++) {
            Cell hCell = headerRow.createCell(i);
            hCell.setCellValue(hs[i]);
            hCell.setCellStyle(styles.get(StyleKey.HEADER));
        }

        // Data
        int rowNum = 2;
        int stt = 1;

        for (CategoryResponse c : data) {
            Row r = sheet.createRow(rowNum++);
            r.setHeight((short) 380);

            cell(r, 0, stt++, styles.get(StyleKey.DATA_CENTER));
            cell(r, 1, safe(c.getCode()), styles.get(StyleKey.DATA_CENTER));
            cell(r, 2, safe(c.getName()), styles.get(StyleKey.DATA));
            cell(r, 3, safe(c.getParentName()), styles.get(StyleKey.DATA));
            cell(r, 4, safe(c.getDescription()), styles.get(StyleKey.DATA));

            cell(r, 5,
                    c.isActive() ? "ACTIVE" : "INACTIVE",
                    c.isActive()
                            ? styles.get(StyleKey.BADGE_ACTIVE)
                            : styles.get(StyleKey.BADGE_INACTIVE)
            );

            cell(r, 6, c.getDisplayOrder(), styles.get(StyleKey.DATA_CENTER));
        }
    }
}
