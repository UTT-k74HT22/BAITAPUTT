package org.example.utils.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.example.dto.response.InventoryResponse;

import java.util.List;

public class InventoryExcelWriter extends AbstractExcelWriter<InventoryResponse> {
    /**
     * Tên sheet trong file Excel.
     *
     * @return tên sheet
     */
    @Override
    public String sheetName() {
        return "Inventory";
    }

    /**
     * Tiêu đề của sheet.
     *
     * @return tiêu đề
     */
    @Override
    public String title() {
        return "Hàng tồn kho";
    }

    /**
     * Các tiêu đề cột trong sheet.
     *
     * @return mảng tiêu đề cột
     */
    @Override
    public String[] headers() {
        return new String[]{
                "STT",
                "Mã sản phẩm",
                "Tên sản phẩm",
                "Số lượng tồn",
                "Đơn vị tính",
                "Kho hàng",
                "Hạn sử dụng",
                "Trạng thái"
        };
    }

    /**
     * Ghi dữ liệu vào sheet.
     *
     * @param sheet  sheet cần ghi
     * @param styles kiểu định dạng Excel
     * @param data   danh sách dữ liệu cần ghi
     */
    @Override
    public void create(Sheet sheet, ExcelStyles styles, List<InventoryResponse> data) {
        // Title row
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 600);

        Cell tCell = titleRow.createCell(0);
        tCell.setCellValue(title());
        tCell.setCellStyle(styles.get(StyleKey.TITLE));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers().length - 1));

        // Header row
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight((short) 420);

        String[] hs = headers();
        for (int i = 0; i < hs.length; i++) {
            Cell hCell = headerRow.createCell(i);
            hCell.setCellValue(hs[i]);
            hCell.setCellStyle(styles.get(StyleKey.HEADER));
        }

        //Data rows
        int rowNum = 2;
        int stt = 1;
        for (InventoryResponse item : data) {
            Row r = sheet.createRow(rowNum++);
            r.setHeight((short) 380);
            cell(r, 0, stt++, styles.get(StyleKey.DATA_CENTER));
            cell(r, 1, item.getProductCode(), styles.get(StyleKey.DATA));
            cell(r, 2, item.getProductName(), styles.get(StyleKey.DATA));
            cell(r, 3, item.getQuantityOnHand(), styles.get(StyleKey.DATA_CENTER));
            cell(r, 4, item.getUnit(), styles.get(StyleKey.DATA_CENTER));
            cell(r, 5, item.getWarehouseName(), styles.get(StyleKey.DATA));
            cell(r, 6, item.getExpiryDate() != null ? item.getExpiryDate().toString() : "", styles.get(StyleKey.DATA_CENTER));
            cell(r, 7, buildStatusText(item), getStatusCellStyle(styles, item));
        }
    }

    private String buildStatusText(InventoryResponse response) {
        if (Boolean.TRUE.equals(response.getIsLowStock())) return "Sắp hết hàng";
        if (Boolean.TRUE.equals(response.getIsNearExpiry())) return "Sắp hết hạn";
        if (Boolean.TRUE.equals(response.getIsOverStock())) return "Quá tồn kho";
        return "Bình thường";
    }

    private CellStyle getStatusCellStyle(ExcelStyles styles, InventoryResponse response) {
        if (Boolean.TRUE.equals(response.getIsLowStock())) {
            return styles.get(StyleKey.BADGE_INACTIVE);
        }
        if (Boolean.TRUE.equals(response.getIsNearExpiry())) {
            return styles.get(StyleKey.BADGE_INACTIVE);
        }
        if (Boolean.TRUE.equals(response.getIsOverStock())) {
            return styles.get(StyleKey.BADGE_INACTIVE);
        }
        return styles.get(StyleKey.BADGE_ACTIVE);
    }
}
