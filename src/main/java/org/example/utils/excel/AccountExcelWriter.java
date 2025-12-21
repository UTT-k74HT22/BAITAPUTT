package org.example.utils.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.example.dto.response.AccountResponse;

import java.util.List;

/**
 * Excel writer cho DTO AccountResponse.
 * @author Hoang Dinh Dung
 */
public class AccountExcelWriter extends AbstractExcelWriter<AccountResponse> {

    @Override
    public String sheetName() {
        return "Accounts";
    }

    @Override
    public String title() {
        return "DANH SÁCH TÀI KHOẢN";
    }

    @Override
    public String[] headers() {
        return new String[]{
                "STT",
                "Account ID",
                "Username",
                "Role",
                "Trạng thái",
                "Last Login"
        };
    }

    @Override
    public void create(Sheet sheet, ExcelStyles styles, List<AccountResponse> data) {
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

        // Data rows
        int rowNum = 2;
        int stt = 1;

        for (AccountResponse a : data) {
            Row r = sheet.createRow(rowNum++);
            r.setHeight((short) 380);

            cell(r, 0, stt++, styles.get(StyleKey.DATA_CENTER));
            cell(r, 1, a.getId(), styles.get(StyleKey.DATA_CENTER));
            cell(r, 2, safe(a.getUsername()), styles.get(StyleKey.DATA));
            cell(r, 3, a.getRole() == null ? "" : a.getRole().name(), styles.get(StyleKey.DATA_CENTER));

            cell(r, 4,
                    a.isActive() ? "ACTIVE" : "INACTIVE",
                    a.isActive() ? styles.get(StyleKey.BADGE_ACTIVE) : styles.get(StyleKey.BADGE_INACTIVE)
            );

            // lastLoginAt: nếu AbstractExcelWriter đã handle LocalDateTime ok thì giữ nguyên.
            // Nếu chưa thì bạn có thể format string trước.
            cell(r, 5, a.getLastLoginAt(), styles.get(StyleKey.DATA_CENTER));
        }
    }
}
